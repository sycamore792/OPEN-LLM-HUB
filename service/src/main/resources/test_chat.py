import requests
import json

# 设置API URL
url = "http://localhost:8888/v1/chat/completions"

# 设置请求头
headers = {
    "Content-Type": "application/json",
    "Authorization": "Bearer sycamore"  # 替换 YOUR_MOONSHOT_API_KEY 为你的API密钥
}

# 设置请求数据
data = {
    "model": "qwen1.5-14b-chat",
    "messages": [
        {"role": "system", "content": "你是 Kimi，由 Moonshot AI 提供的人工智能助手，你更擅长中文和英文的对话。你会为用户提供安全，有帮助，准确的回答。同时，你会拒绝一切涉及恐怖主义，种族歧视，黄色暴力等问题的回答。Moonshot AI 为专有名词，不可翻译成其他语言。"},
        {"role": "user", "content": "你好，我叫李雷，1+1等于多少？"}
    ],
    "temperature": 0.3,
    "stream": True
}

# 发送POST请求
response = requests.post(url, headers=headers, json=data,stream=True)
for i in response.iter_lines():

    try:
        if i.decode("utf-8"):
            loads = json.loads(i.decode("utf-8")[6:])
            print(loads["choices"][0]["delta"]["content"],end='')
    except Exception as e:
        pass