<template>
    <div class="chat-container">

        <div class="header">
            <div style="display: inline-block;">
                <img style="width: 40px;" src="@/assets/logo_white.png">
                <h2 style="display: inline;vertical-align: middle;">Playground </h2>
                <span style="padding-left: 10px; padding-right: 10px">模型</span>
                {{modelName}}
                <el-select
                    clearable
                    v-model="modelName"
                    @change="onModelChange"
                    value-key="id"
                    placeholder="请选择模型"
                    effect="dark"
                    style="width: 240px"
                >
                    <el-option
                        v-for="item in modelList"
                        :key="item.id"
                        :label="item.modelName"
                        :value="item.modelName"
                    >
                        {{ item.modelName }}
                    </el-option>
                </el-select>
            </div>
        </div>
        <div class="content-area">
            <div class="chat-area">
                <div class="message-area">
                    <div class="message">
                        <span class="role">system</span>
                        <div contenteditable="true" v-html="systemMsg" class="content-input"
                             @input="updateSystemMsg"></div>
                    </div>
                    <div v-for="(item, index) in messages" :key="index" class="message" >
                        <span class="role">{{ item.role }}</span>
                        <div contenteditable="true" @input="updateMessageContent(item)"   class="content-input">
                            <transition-group name="fade">
                                <div>
                                    {{item.content}}
                                </div>
<!--                            <span v-for="(char, index) in item.content" :key="index" class="chat-word">-->
<!--                                {{ char }}-->
<!--                            </span>-->
                            </transition-group>

                        </div>
                        <img class="delete-icon" alt="" src="@/assets/delete_icon.png"
                             @click="messages.splice(index, 1)">
                    </div>
                </div>
                <div class="input-area" v-loading="inputLoading"  element-loading-background="rgba(0,0,0,0.5)">
                    <div class="send-role" @click="switchSendRole">{{ this.sendRole }}</div>
                    <input type="text" v-model="userMessage" class="send-input">
                    <button v-show="modelName!==''"  class="send-button" @click="sendMessage"><img src="@/assets/Arrow_Top.png"></button>
                </div>
            </div>
            <div class="setting-area">
                <div class="slider-container">
                    <label for="temperature">Temperature</label>

                    <input type="range" id="temperature" v-model="temperature.value" :min="temperature.minValue"
                           :max="temperature.maxValue" :step="temperature.step">
                    <span class="slider-value">{{ temperature.value }}</span>
                </div>
                <div class="slider-container">
                    <label for="max-tokens">Max Tokens</label>

                    <input type="range" id="max-tokens" v-model="maxTokens.value" :min="maxTokens.minValue"
                           :max="maxTokens.maxValue" :step="maxTokens.step">
                    <span class="slider-value">{{ maxTokens.value }}</span>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import {fetchEventSource} from "@microsoft/fetch-event-source";
import keyApi from "@/api/KeyApi";

export default {
    name: "Chat",
    data() {
        return {
            modelList: [
            ],
            modelName: '',
            apiKey:"",
            inputLoading:false,
            systemMsg: "这是一个系统提示",
            userMessage: "",
            sendRole: "user",
            temperature: {
                minValue: 0,
                maxValue: 1,
                step: 0.01,
                value: 0.5
            },
            maxTokens: {
                minValue: 1,
                maxValue: 4096,
                step: 1,
                value: 1024
            },
            messages: [],
        }
    },
    methods: {
        getModelList(apiKey) {
            keyApi.getModelPageList(1, 100,apiKey).then(res => {
                this.modelList = res.data.records

            })
        },
        onModelChange() {
            // 当选择的模型改变时，可以在这里添加一些逻辑
            // 可以根据选择的模型更新其他设置，比如 maxTokens
            if (this.selectedModel) {
                this.maxTokens.maxValue = this.selectedModel.maxTokens || 4096;
                this.maxTokens.value = Math.min(this.maxTokens.value, this.maxTokens.maxValue);
            }
        },
        sendMessage() {
            this.inputLoading = true;
            let msgList = [];
            if (this.systemMsg.trim() !== "") {
                msgList.push({role: "system", content: this.systemMsg});
            }
            if (this.userMessage.trim() !== "") {
                this.messages.push({role: "user", content: this.userMessage});
                this.userMessage = "";
                this.messages.forEach(item => {
                    msgList.push({role: item.role, content: item.content});
                });
                this.messages.push({role: "assistant", content: ""});
            }
            const ctrl = new AbortController();
            const _this = this;
            fetchEventSource('http://localhost/v1/chat/completions', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer '+_this.apiKey // 请确保在实际使用时填入正确的授权信息
                },
                body: JSON.stringify({
                    "model": _this.modelName,
                    "messages": msgList,
                    "temperature": _this.temperature.value,
                    "max_tokens": _this.maxTokens.value,
                    "stream": true
                }),
                signal: ctrl.signal,
                onopen: (res) => {

                },
                onmessage: (msg) => {
                    try {
                        let data = JSON.parse(msg.data);
                        const word = data['choices'][0]['delta']['content'];
                        if (word) {
                            this.messages[this.messages.length - 1].content += word;
                        }
                    } catch (e) {

                    }
                },
                onclose: () => {
                    _this.inputLoading = false;
                },
                onerror: (err) => {
                    _this.inputLoading = false;
                    console.error('Error:', err);
                    ctrl.abort();
                }
            });
        },
        switchSendRole() {
            if (this.sendRole === 'user') {
                this.sendRole = "assistant";
            } else {
                this.sendRole = "user";
            }
        },
        updateSystemMsg(event) {
            // Update the system message when it's edited
            this.systemMsg = event.target.innerText;
        },

        updateMessageContent(item) {
            // This method will be called when a message's content is edited
            // We need to find the index of the item in the messages array
            const index = this.messages.findIndex(message => message === item);
            if (index !== -1) {
                // Update the content of the message
                this.$set(this.messages, index, {
                    ...item,
                    content: event.target.innerText
                });
            }
        },
    },
    created() {
        //获取用户id
        this.apiKey = this.$route.params.apiKey;

    },
    mounted() {
        this.getModelList(this.apiKey);
    }
}
</script>

<style scoped>

.content-input {
    flex: 1;
    background: transparent;
    border: none;
    color: white;
    font-size: inherit;
    font-family: inherit;
    cursor: text;
    outline: none;
    white-space: pre-wrap;
    transition: all 0.3s ease;
}

.content-input:focus {
    border-radius: 10px;
    background-color: rgba(255, 255, 255, 0.1);
}

.content-input[contenteditable="false"] {
    cursor: default;
}

.chat-word {
    color: white;
    display: inline-block;
    opacity: 0;
    transform: translateY(10px);
    animation: fadeInUp 0.3s forwards;
}

.chat-container {
    display: flex;
    flex-direction: column;
    background-color: #1f1f1f;
    height: 100vh;
}

.header {
    padding: 1rem;
    color: white;
    border-bottom: 1px solid #444;
}

.content-area {
    display: flex;
    flex: 1;
}

.chat-area {
    flex: 0 0 85%;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
}

.setting-area {
    flex: 0 0 15%;
    border-left: 1px solid #444;
    padding: 1rem;
}

.slider-container {
    width: 80%;
    margin: 0 auto;
}

.slider-container label {
    display: block;
    color: white;
    margin-bottom: 0.5rem;
}

.slider-container input[type="range"] {
    width: 100%;
    -webkit-appearance: none;
    appearance: none;
    background: #333;
    height: 6px;
    border-radius: 5px;
    outline: none;
    opacity: 0.7;
    transition: opacity .2s;
}

.slider-container input[type="range"]::-webkit-slider-thumb {
    -webkit-appearance: none;
    appearance: none;
    width: 20px;
    height: 20px;
    border-radius: 50%;
    background: #fff;
    cursor: pointer;
}

.slider-container input[type="range"]::-moz-range-thumb {
    width: 20px;
    height: 20px;
    border-radius: 50%;
    background: #fff;
    cursor: pointer;
}

.slider-value {
    display: block;
    color: white;
    text-align: right;
    margin-top: 0.5rem;
}

.message-area {
    overflow-y: auto;
}

.send-role {
    color: #ffffff;
    font-weight: bolder;
    margin-left: 1rem;
    border-radius: 10px;
    background-color: #5e5d5d;
    width: 85px;
    line-height: 49px;
    text-align: center;
}

.message {
    margin-bottom: 1px;
    padding: 1.5rem;
    border-radius: 10px;
    border: 1px solid white;
    display: flex;
    align-items: flex-start;
    position: relative;
    transition: background-color 0.5s ease;
}

.message:hover {
    background-color: rgba(255, 255, 255, 0.1);
}

.role {
    font-weight: bold;
    color: white;
    width: 4rem;
    margin-right: 4rem;
}

.input-area {
    display: flex;
    align-items: center;
    padding: 1rem;
    border-top: 1px solid #444;
}

.input-area span {
    font-weight: bold;
    color: white;
    margin-right: 1rem;
}

.send-input {
    margin-left: 2rem;
    margin-right: 2rem;
    flex: 1;
    background: #444;
    border: 1px solid #666;
    color: white;
    padding: 1rem;
    border-radius: 10px;
    outline: none;
}

.send-button {
    background: #ffffff;
    border: none;
    color: #000000;
    padding: 0.5rem 1rem;
    margin-left: 0.5rem;
    border-radius: 5px;
    cursor: pointer;
    transition: background-color 0.3s;
}

.send-button:hover {
    background: #adacac;
}

.delete-icon {
    display: none;
    position: absolute;
    right: 0;
    top: 0;
    width: 24px;
    height: 24px;
}

.message:hover .delete-icon {
    display: block;
}
</style>
