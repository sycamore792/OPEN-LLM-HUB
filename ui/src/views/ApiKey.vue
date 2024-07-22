<template>
   <div class="page-container">
       <div class="content-wrapper">

           <div class="table-container">
               <el-table :fit="false" :data="tableData" style="width: 70%">
                   <el-table-column label="创建人" width="180">
                       <template #default="scope">
                           <div style="display: flex; align-items: center">
                               <span>桑某昌</span>
                               <!--                        <span>{{ scope.row.username }}</span>-->
                           </div>
                       </template>
                   </el-table-column>
                   <el-table-column show-overflow-tooltip label="ApiKey" width="400px">
                       <template #default="scope">
                           <div style="display: flex; align-items: center">
                               <span>{{ scope.row.apiKey }}</span>
                           </div>
                       </template>
                   </el-table-column>
                   <el-table-column label="创建时间" width="200px">
                       <template #default="scope">
                           <div style="display: flex; align-items: center">
                               <span>{{ DateUtil.dateFormat(scope.row.createTime) }}</span>
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
import {Timer} from '@element-plus/icons-vue'
import keyApi from "@/api/KeyApi";
import DateUtil from "@/utils/DateUtil";
import ModelApi from "@/api/ModelApi";

export default {
    name: "ApiKey",
    computed: {
        DateUtil() {
            return DateUtil
        }
    },
    components: {Timer},
    created() {
      this.searchData()
    },
    data() {
        return {
            currentPage: 1,
            dataTotal: 1,
            pageSize: 8,
            tableData: [

            ]
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
    }
}
</script>

<style scoped>
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
