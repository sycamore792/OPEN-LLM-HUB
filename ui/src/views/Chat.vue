<template>
    <div class="chat-container">

        <div class="header">
            <div style="display: inline-block;">
                <img style="width: 40px;" src="@/assets/logo_white.png">
                <h2 style="display: inline;vertical-align: middle;">Playground </h2>
                <span style="padding-left: 10px; padding-right: 10px">模型</span>

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

                <el-button
                        style="margin-left: 10px;"
                        size="small"
                        @click="showLogDialog = true"
                >
                    显示日志
                </el-button>
            </div>
            <el-button
                class="back-button"
                type="text"
                @click="goBack"
            >
                返回
            </el-button>
        </div>
        <div class="content-area">
            <div class="chat-area">
                <div class="message-area" ref="messageArea" @scroll="onScroll">
                    <div class="message">
                        <span class="role">system</span>
                        <div
                                class="content-input"
                                contenteditable="true"
                                @input="updateSystemMsg($event)"
                                @blur="onBlur($event)"
                                v-html="systemMsg"
                        ></div>
                    </div>
                    <div v-for="(item, index) in messages" :key="index" class="message">
                        <span class="role">{{ item.role }}</span>
                        <div
                                class="content-input"
                                contenteditable="true"
                                @input="updateMessageContent($event, item)"
                                @blur="onBlur($event)"
                                v-html="item.content"
                        ></div>
                        <img class="delete-icon" alt="" src="@/assets/delete_icon.png"
                             @click="messages.splice(index, 1)">
                    </div>
                    <button v-show="showScrollButton" @click="scrollToBottom" class="scroll-to-bottom">
                        👇🏼 滚动到底部
                    </button>
                </div>
                <div class="input-area" v-loading="inputLoading" element-loading-background="rgba(0,0,0,0.5)">
                    <div class="send-role" @click="switchSendRole">{{ this.sendRole }}</div>
                    <textarea
                            v-model="userMessage"
                            class="send-input"
                            @keydown="handleKeyDown"
                            :placeholder="modelName ? '输入消息，按回车发送，Shift+回车换行' : '请先选择模型'"
                            :disabled="!modelName"
                    ></textarea>
                    <button
                            class="send-button"
                            @click="sendMessage"
                            :disabled="!modelName || !userMessage.trim()"
                            :title="!modelName ? '请先选择模型' : (!userMessage.trim() ? '请输入消息' : '发送')"
                    >
                        <img src="@/assets/Arrow_Top.png">
                    </button>
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

        <el-dialog
                title="完成日志"
                v-model="showLogDialog"
                width="70%"
        >
            <el-table :data="completionsLogs" style="width: 100%">
                <el-table-column prop="timestamp" label="时间戳" width="180"></el-table-column>
                <el-table-column prop="model" label="模型" width="120"></el-table-column>
                <el-table-column prop="prompt" label="提示" show-overflow-tooltip></el-table-column>
                <el-table-column prop="response" label="响应" show-overflow-tooltip></el-table-column>
                <el-table-column prop="usage.total_tokens" label="tokens消耗" show-overflow-tooltip></el-table-column>
            </el-table>
        </el-dialog>
    </div>
</template>

<script>
import {fetchEventSource} from "@microsoft/fetch-event-source";
import keyApi from "@/api/KeyApi";
import axiosInstance from "@/api/BaseApi";

export default {
    name: "Chat",
    data() {
        return {
            modelList: [],
            modelName: '',
            apiKey: "",
            inputLoading: false,
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
            showScrollButton: false,
            completionsLogs: [],
            showLogDialog: false,
        }
    },
    methods: {
        goBack() {
            this.$router.go(-1);
        },
        handleKeyDown(event) {
            if (event.key === 'Enter' && !event.shiftKey) {
                event.preventDefault();
                if (this.modelName && this.userMessage.trim()) {
                    this.sendMessage();
                }
            }
        },
        onScroll() {
            const element = this.$refs.messageArea;
            const atBottom = element.scrollHeight - element.scrollTop <= element.clientHeight + 100; // 添加一些容差
            this.showScrollButton = !atBottom;
        },
        scrollToBottom() {
            const element = this.$refs.messageArea;
            element.scrollTop = element.scrollHeight;
            this.showScrollButton = false;
        },
        getModelList(apiKey) {
            keyApi.getModelPageList(1, 100, apiKey).then(res => {
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
            if (!this.modelName || this.userMessage.trim() === '') return;

            const query = this.userMessage;
            this.inputLoading = true;
            let msgList = [];
            if (this.systemMsg.trim() !== "") {
                msgList.push({role: "system", content: this.systemMsg});
            }

            // Add user message
            this.messages.push({role: this.sendRole, content: this.userMessage});

            // Build message list
            this.messages.forEach(item => {
                msgList.push({role: item.role, content: item.content});
            });


            // Add a new empty assistant message
            this.messages.push({role: "assistant", content: ""});

            const ctrl = new AbortController();
            const _this = this;
            const completionsLog = {
                prompt: msgList[msgList.length-1].content,
            }


            fetchEventSource(axiosInstance.getUri() + '/v1/chat/completions', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + _this.apiKey
                },
                body: JSON.stringify({
                    "model": _this.modelName,
                    "messages": msgList,
                    "temperature": _this.temperature.value,
                    "max_tokens": _this.maxTokens.value,
                    "stream": true
                }),
                openWhenHidden: true,
                signal: ctrl.signal,
                onopen: (res) => {
                    // Logic for when connection opens
                },
                onmessage: (msg) => {
                    try {
                        let data = JSON.parse(msg.data);
                        if (data.usage) {
                            completionsLog['usage'] = data['usage'];
                        }
                        const word = data['choices'][0]['delta']['content'];
                        if (word) {
                            // Update the content of the last message (the new assistant message)
                            this.messages[this.messages.length - 1].content += word;
                        }
                    } catch (e) {
                        // console.error('Error parsing message:', e);
                    }
                },
                onclose: () => {
                    _this.inputLoading = false;
                    // Add log entry
                    completionsLog.timestamp = new Date().toLocaleString();
                    completionsLog.model = _this.modelName;
                    completionsLog.response = _this.messages[_this.messages.length - 1].content;

                    _this.completionsLogs.push(
                        {
                            ...completionsLog,
                            id: _this.completionsLogs.length + 1
                        }
                    );
                },
                onerror: (err) => {
                    _this.inputLoading = false;
                    console.error('Error:', err);
                    ctrl.abort();
                }
            });

            this.userMessage = ""; // Clear input box
            this.$nextTick(() => {
                this.scrollToBottom();
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
            this.updateContent(event, 'systemMsg');
        },
        updateMessageContent(event, item) {
            this.updateContent(event, 'content', item);
        },
        updateContent(event, prop, item = null) {
            const selection = window.getSelection();
            const range = selection.getRangeAt(0);
            const start = range.startOffset;
            const end = range.endOffset;

            const content = event.target.innerHTML;
            if (item) {
                item[prop] = content;
            } else {
                this[prop] = content;
            }

            this.$nextTick(() => {
                if (content.trim() !== '') {
                    try {
                        const newRange = document.createRange();
                        const childNode = event.target.firstChild || event.target;
                        const nodeType = childNode.nodeType;

                        if (nodeType === Node.TEXT_NODE) {
                            newRange.setStart(childNode, Math.min(start, childNode.length));
                            newRange.setEnd(childNode, Math.min(end, childNode.length));
                        } else {
                            newRange.setStart(event.target, 0);
                            newRange.setEnd(event.target, 0);
                        }

                        selection.removeAllRanges();
                        selection.addRange(newRange);
                    } catch (error) {
                        console.error('Error setting cursor position:', error);
                    }
                } else {
                    // If content is empty, just focus on the element
                    event.target.focus();
                }
            });
        },
        onBlur(event) {
            // 移除可能导致问题的空白段落
            event.target.innerHTML = event.target.innerHTML.replace(/<p><br><\/p>/g, '<br>');
        },
    },
    created() {
        //获取用户id
        this.apiKey = this.$route.params.apiKey;

    },
    mounted() {
        this.getModelList(this.apiKey);
        this.$nextTick(() => {
            this.scrollToBottom();
        });
    },
    updated() {
        if (!this.showScrollButton) {
            this.$nextTick(() => {
                this.scrollToBottom();
            });
        }
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
    min-height: 1em; /* 确保空内容时也有高度 */
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
    overflow: hidden; /* Prevent scrolling on the main container */
}

.header {
    padding: 1rem;
    color: white;
    border-bottom: 1px solid #444;
    display: flex;
    justify-content: space-between;
    align-items: center;
}
.back-button {
    color: white;
    font-size: 14px;
}
.content-area {
    display: flex;
    flex: 1;
    overflow: hidden; /* Prevent scrolling on content area */
}

.chat-area {
    flex: 0 0 85%;
    display: flex;
    flex-direction: column;
    overflow: hidden; /* Prevent scrolling on chat area */
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
    flex: 1;
    overflow-y: auto;
    padding: 1rem;
    scroll-behavior: smooth;
    position: relative; /* 为绝对定位的子元素提供参考 */
}

.scroll-to-bottom {
    position: sticky;
    bottom: 20px;
    left: 50%;
    transform: translateX(-50%);
    background-color: rgba(45, 45, 45, 0.9);
    color: white;
    border: none;
    border-radius: 20px;
    padding: 10px 20px;
    font-size: 14px;
    cursor: pointer;
    transition: opacity 0.3s ease, background-color 0.3s ease;
    display: flex;
    align-items: center;
    justify-content: center;
    opacity: 0.7;
    z-index: 10;
}

.scroll-to-bottom:hover {
    opacity: 1;
    background-color: rgba(60, 60, 60, 0.9);
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
    resize: none;

    max-height: 150px;
    overflow-y: auto;
}

.send-button {
    background: #ffffff;
    border: none;
    color: #000000;
    padding: 0.5rem 1rem;
    margin-left: 0.5rem;
    border-radius: 5px;
    cursor: pointer;
    transition: background-color 0.3s, opacity 0.3s;
}

.send-button:hover:not(:disabled) {
    background: #adacac;
}

.send-button:hover {
    background: #adacac;
}

.send-button:disabled {
    opacity: 0.5;
    cursor: not-allowed;
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
