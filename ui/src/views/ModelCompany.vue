<template>
    <div class="page-container">
        <div class="content-wrapper">
            <div class="--el-box-shadow table-container">
                <el-table  :data="tableData" style="width: 70%">
                    <el-table-column label="Company Name (zh)" >
                        <template #default="scope">
                            <div style="display: flex; align-items: center">
                                <img :src="scope.row.companyIcon" alt="Icon" class="icon"
                                     style="width: 20px; height: 20px; margin-right: 10px;">
                                <span>{{ scope.row.companyNameZh }}</span>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column label="Company Name (en)" >
                        <template #default="scope">
                            <div style="display: flex; align-items: center">
                                <span style="margin-left: 10px">{{ scope.row.companyNameEn }}</span>
                            </div>
                        </template>
                    </el-table-column>
                </el-table>
            </div>
        </div>
    </div>

</template>

<script>
import {Timer} from '@element-plus/icons-vue'
import ModelApi from "@/api/ModelApi";

export default {
    name: "ModelCompany",
    components: {Timer},
    created() {
        ModelApi.getModelCompanyList().then(res => {
            this.tableData = res.data.data
        })
    },
    data() {
        return {
            tableData: []
        }
    },
    methods: {
        handleEdit(index, row) {
            console.log(index, row);
        },
        handleDelete(index, row) {
            console.log(index, row);
        }
    }
}
</script>

<style scoped>
.table-container {
    padding-top: 50px;
    display: flex;
    justify-content: center;
    width: 100%;
}
.page-container {
    display: flex;
    justify-content: center;
    width: 100%;
}.content-wrapper {
     width: 100%;
     max-width: 90%; /* 或者您认为合适的最大宽度 */
     padding: 20px;
 }
.icon {
    width: 20px;
    height: 20px;
    object-fit: contain;
    background: transparent; /* Ensure background is transparent */

}
</style>
