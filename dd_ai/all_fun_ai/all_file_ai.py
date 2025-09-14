
from transformers import BertTokenizer, BertForSequenceClassification
import torch
import json
import requests
from datetime import datetime
import os
import logging
import time
import traceback
from aip import AipSpeech
from flask import Flask, request, jsonify, send_file
from pydub import AudioSegment
from flask_cors import CORS
from io import BytesIO

app = Flask(__name__)
CORS(app,
    resources={r"/api/*": {
        "origins": ["*", "null"],  # 显式允许null源
        "methods": ["POST", "OPTIONS"],
        "allow_headers": ["Content-Type"]
    }},
    supports_credentials=True
)

device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
print(device)

# 加载训练好的模型
model_path = "./model/best_model.bin"
model = BertForSequenceClassification.from_pretrained("./chinese-roberta-wwm-ext", num_labels=4)
model.load_state_dict(torch.load(model_path, map_location=device))
model.to(device)
model.eval()
# print(model)
# 添加标签映射（与训练代码一致）
label_map = {
    0: "愉悦",
    1: "平静",
    2: "孤独",
    3: "焦虑"
}

tokenizer = BertTokenizer.from_pretrained("./chinese-roberta-wwm-ext")


@app.route('/api/predict', methods=['POST'])
def predict():
    text = request.json['text']

    # 预处理
    inputs = tokenizer(
        text,
        padding="max_length",
        truncation=True,
        max_length=512,
        return_tensors="pt"
    ).to(device)

    # 预测
    with torch.no_grad():
        outputs = model(**inputs)

    probs = torch.nn.functional.softmax(outputs.logits, dim=1)
    print(probs)
    prediction = torch.argmax(probs, dim=1).item()
    print(prediction)

    return jsonify({
        "text": text,
        "label": int(prediction),
        "label_text": label_map[prediction],  # 添加文本标签
        "confidence": probs[0][prediction].item(),
        "probabilities": {  # 返回所有类别概率
            "愉悦": probs[0][0].item(),
            "平静": probs[0][1].item(),
            "孤独": probs[0][2].item(),
            "焦虑": probs[0][3].item()
        }
    })


# ===================== 配置区 =====================
# 配置日志记录
logging.basicConfig(level=logging.DEBUG)
logger = logging.getLogger(__name__)

API_KEY = "aW7NA0FwLrPGktomTqnvgFN6"
SECRET_KEY = "j0GRU8EqgFKdjhINsP7OugIkD86nSeuA"
OUTPUT_FILE = "output/dialogue_history.txt"


def get_access_token():
    """获取百度API访问令牌"""
    try:
        url = "https://aip.baidubce.com/oauth/2.0/token"
        params = {
            "grant_type": "client_credentials",
            "client_id": API_KEY,
            "client_secret": SECRET_KEY
        }
        response = requests.post(url, params=params)
        response.raise_for_status()  # 检查HTTP错误
        return str(response.json().get("access_token"))
    except Exception as e:
        logger.error(f"获取access_token失败: {str(e)}")
        logger.debug(traceback.format_exc())
        raise  # 将异常向上传递


@app.route('/api/chat', methods=['POST'])
def chat_api():
    """提供对话服务的API接口"""
    try:
        # 记录完整请求体
        request_data = request.get_json()
        logger.debug(f"收到请求数据: {json.dumps(request_data, ensure_ascii=False)}")

        user_input = request_data.get("message", "")
        if not user_input:
            return jsonify({"status": "error", "message": "空输入内容"}), 400

        # 获取access_token
        start_time = datetime.now()
        access_token = get_access_token()
        logger.debug(f"获取access_token耗时: {(datetime.now() - start_time).total_seconds():.2f}s")

        # 构造请求
        url = f"https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/completions_pro?access_token={access_token}"
        payload = {
            "messages": [{
                "role": "user",
                "content": f"你是一个专业的情感支持助手，请用温暖缓慢的语气回复。用户输入：{user_input}"
            }]
        }

        headers = {'Content-Type': 'application/json'}

        # 发送请求（记录完整请求内容）
        logger.debug(f"请求URL: {url}")
        logger.debug(f"请求头: {headers}")
        logger.debug(f"请求体: {json.dumps(payload, ensure_ascii=False)}")

        start_time = datetime.now()
        response = requests.post(url, headers=headers, json=payload, timeout=30)
        response.raise_for_status()

        # 解析响应
        result = response.json()
        logger.debug(f"API响应耗时: {(datetime.now() - start_time).total_seconds():.2f}s")
        logger.debug(f"原始响应内容: {json.dumps(result, ensure_ascii=False)}")

        # 错误处理
        if "error_code" in result:
            error_msg = f"API错误 {result['error_code']}: {result.get('error_msg', '未知错误')}"
            logger.error(error_msg)
            return jsonify({"status": "error", "message": error_msg}), 500

        answer = result.get("result", "暂时无法回答这个问题")
        answer = answer.replace('\n', ' ').strip()

        # 记录对话日志
        timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
        with open(OUTPUT_FILE, "a", encoding="utf-8") as f:
            f.write(f"[{timestamp}]\n问：{user_input}\n答：{answer}\n{'=' * 30}\n")

        return jsonify({
            "status": "success",
            "answer": answer,
            "timestamp": timestamp
        })

    except requests.exceptions.Timeout:
        logger.error("API请求超时")
        return jsonify({"status": "error", "message": "请求超时，请稍后再试"}), 504

    except requests.exceptions.HTTPError as e:
        logger.error(f"HTTP错误: {e.response.status_code}")
        logger.debug(f"错误响应内容: {e.response.text}")
        return jsonify({
            "status": "error",
            "message": f"HTTP错误 {e.response.status_code}",
            "response": e.response.text
        }), 500

    except json.JSONDecodeError:
        logger.error("响应解析失败")
        logger.debug(f"原始响应内容: {response.text}")
        return jsonify({
            "status": "error",
            "message": "服务响应格式错误"
        }), 500

    except Exception as e:
        logger.error(f"未处理异常: {str(e)}")
        logger.debug(traceback.format_exc())
        return jsonify({
            "status": "error",
            "message": f"服务器内部错误: {str(e)}",
            "traceback": traceback.format_exc()
        }), 500


# 配置参数
SAVE_DIR = r"test_voice"
APP_ID_1 = '117845551'
API_KEY_1 = 'ZQZ8pehii9XPog22DiloIxRg'
SECRET_KEY_1 = 'JwMX4ufimyR8yf8SqlFfEj3S4zpgrjnc'

client = AipSpeech(APP_ID_1, API_KEY_1, SECRET_KEY_1)

os.makedirs(SAVE_DIR, exist_ok=True)


@app.route('/api/speech', methods=['POST'])
def handle_audio():
    try:
        logging.info("=== 收到新请求 ===")

        # 检查文件接收
        if 'audio' not in request.files:
            logging.error("未收到音频文件")
            return jsonify({"status": "error", "msg": "未收到音频文件"}), 400

        audio_file = request.files['audio']
        logging.info(f"收到文件: {audio_file.filename} 大小: {len(audio_file.read())}字节")
        audio_file.seek(0)  # 重置文件指针

        # 保存原始文件（调试用）
        debug_path = os.path.join(SAVE_DIR, 'debug_recording.webm')
        audio_file.save(debug_path)
        logging.info(f"原始文件已保存到: {debug_path}")

        # 转换格式（添加详细日志）
        try:
            audio = AudioSegment.from_file(debug_path, format="webm")  # 明确指定格式
            logging.info("原始文件信息: ")
            logging.info(f"时长: {len(audio) / 1000}s, 声道: {audio.channels}, 采样率: {audio.frame_rate}Hz")

            converted = audio.set_channels(1).set_frame_rate(16000).set_sample_width(2)
            wav_path = os.path.join(SAVE_DIR, f"final_{int(time.time())}.wav")
            converted.export(wav_path, format="wav", parameters=["-acodec", "pcm_s16le"])
            logging.info(f"转换完成: {wav_path}")

        except Exception as e:
            logging.error(f"格式转换失败: {traceback.format_exc()}")
            return jsonify({"status": "error", "msg": "音频处理失败"}), 500

        # 调用百度API（添加密钥验证）
        try:
            with open(wav_path, 'rb') as f:
                audio_data = f.read()

            logging.info(f"准备识别，文件大小: {len(audio_data)}字节")
            result = client.asr(audio_data, 'wav', 16000, {'dev_pid': 1537})
            logging.info(f"百度返回原始数据: {result}")

            if result['err_no'] != 0:
                logging.error(f"百度API错误: {result['err_msg']}({result['err_no']})")
                return jsonify({"status": "error", "msg": "识别服务返回错误"}), 500

        except Exception as e:
            logging.error(f"API调用失败: {traceback.format_exc()}")
            return jsonify({"status": "error", "msg": "识别服务不可用"}), 500

        return jsonify({
            "status": "success",
            "text": result['result'][0],
            "file_path": wav_path
        }), 200, {'Content-Type': 'application/json'}

    except Exception as e:
        logging.error(f"全局异常: {traceback.format_exc()}")
        return jsonify({"status": "error", "msg": "服务器内部错误"}), 500, {'Content-Type': 'application/json'}


@app.route('/api/synthesize', methods=['POST'])
def synthesize():
    try:
        data = request.json
        text = data.get('text', '')

        # 调用百度语音合成
        result = client.synthesis(
            text,
            'zh',
            1,
            {
                'vol': data.get('volume', 5),
                'spd': data.get('speed', 5),
                'pit': data.get('pitch', 5),
                'per': data.get('person', 0)
            }
        )

        if isinstance(result, dict):
            return {'error': '语音合成失败'}, 500

        # 返回音频流
        return send_file(
            BytesIO(result),
            mimetype='audio/mp3',
            as_attachment=False
        )

    except Exception as e:
        return {'error': str(e)}, 500


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)

