<template>
    <div class="page-container">
        <div class="content-wrapper">
            <div class="table-container">
                <el-table :fit="false" :data="tableData" style="width: 70%">
                    <el-table-column label="创建人" width="180">
                        <template #default="scope">
                            <div style="display: flex; align-items: center">
                                <span class="table-item">桑某昌</span>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column show-overflow-tooltip label="ApiKey" width="400px">
                        <template #default="scope">
                            <div style="display: flex; align-items: center">
                                <span class="table-item">{{ maskApiKey(scope.row.apiKey) }}</span>
                                <el-tooltip content="点击复制完整 ApiKey" placement="top">
                                    <el-button type="text" @click="copyApiKey(scope.row.apiKey)">
                                        <el-icon><DocumentCopy /></el-icon>
                                    </el-button>
                                </el-tooltip>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column label="创建时间" width="200px">
                        <template #default="scope">
                            <div style="display: flex; align-items: center">
                                <span class="table-item">{{ DateUtil.dateFormat(scope.row.createTime) }}</span>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column label="备注" width="200px">
                        <template #default="scope">
                            <div style="display: flex; align-items: center">
                                <span>{{ scope.row.remark }}</span>
                            </div>
                        </template>
                    </el-table-column>
                </el-table>
            </div>

            <div class="pagination-container">
                <el-pagination
                    @size-change="handleSizeChange"
                    @current-change="handleCurrentChange"
                    :current-page="currentPage"
                    :page-sizes="[5, 10, 20, 50]"
                    :page-size="pageSize"
                    layout="total, sizes, prev, pager, next, jumper"
                    :total="dataTotal"
                >
                </el-pagination>
            </div>
        </div>
    </div>
</template>

<script>
import { Timer, DocumentCopy } from '@element-plus/icons-vue'
import keyApi from "@/api/KeyApi";
import DateUtil from "@/utils/DateUtil";
import { ElMessage } from 'element-plus';

export default {
    name: "ApiKey",
    computed: {
        DateUtil() {
            return DateUtil
        }
    },
    components: { Timer, DocumentCopy },
    created() {
        this.searchData()
    },
    data() {
        return {
            currentPage: 1,
            dataTotal: 1,
            pageSize: 8,
            tableData: []
        }
    },
    methods: {
        searchData() {
            keyApi.getApiKeyList(this.currentPage, this.pageSize).then(res => {
                this.tableData = res.data.records
                this.dataTotal = res.data.total
            })
        },
        handleSizeChange(val) {
            this.pageSize = val;
            this.searchData()
        },
        handleCurrentChange(val) {
            this.currentPage = val;
            this.searchData()
        },
        maskApiKey(apiKey) {
            if (apiKey.length <= 8) {
                return '*'.repeat(apiKey.length);
            }
            return apiKey.substring(0, 4) + '*'.repeat(apiKey.length - 8) + apiKey.substring(apiKey.length - 4);
        },
        copyApiKey(apiKey) {
            navigator.clipboard.writeText(apiKey).then(() => {
                ElMessage({
                    message: 'ApiKey 已复制到剪贴板',
                    type: 'success',
                });
            }).catch(() => {
                ElMessage({
                    message: '复制失败，请手动复制',
                    type: 'error',
                });
            });
        }
    }
}
</script>

<style scoped>
.table-item {
    font-weight: bolder;
    font-size: 16px;
}

.page-container {
    display: flex;
    justify-content: center;
    width: 100%;
}

.content-wrapper {
    width: 100%;
    max-width: 1500px; /* 或者您认为合适的最大宽度 */
    padding: 20px;
}

.table-container {
    padding-top: 50px;
    display: flex;
    justify-content: center;
    width: 100%;
}


.pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: center;
}
</style>
