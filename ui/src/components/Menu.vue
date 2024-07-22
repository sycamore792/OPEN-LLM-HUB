<template>
    <div class="sidebar" :class="{ 'is-collapsed': isCollapse }">
        <div class="logo">
            <img style="width: 100px" src="@/assets/logo.png" alt="logo" />
        </div>
        <el-menu
            default-active="1"
                class="sidebar-menu"
                :collapse="isCollapse"
                @open="handleOpen"
                @close="handleClose"
        >
            <el-menu-item @click="toPage('/home')" index="1">
                <el-icon><DataAnalysis /></el-icon>
                <span class="menu-item-text">主页</span>
            </el-menu-item>
            <el-menu-item @click="toPage('/model')" index="2">
                <el-icon><Box /></el-icon>
                <span class="menu-item-text">模型资源库</span>
            </el-menu-item>
            <el-menu-item @click="toPage('/modelCompany')" index="3">
                <el-icon><OfficeBuilding /></el-icon>
                <span class="menu-item-text" >模型厂商</span>
            </el-menu-item>
            <el-menu-item @click="toPage('/apiKey')" index="4">
                <el-icon><Key /></el-icon>
                <span class="menu-item-text">渠道管理</span>
            </el-menu-item>
            <el-menu-item index="5" disabled>
<!--                <el-icon><document /></el-icon>-->
                <el-icon><setting /></el-icon>
                <span>...</span>
            </el-menu-item>
        </el-menu>
        <div class="avatar">
            <!-- 头像可以在这里添加 -->
        </div>
        <div class="collapse-btn-container">
            <el-button
                    class="collapse-btn"
                    @click="toggleCollapse"
                    :icon="isCollapse ? Expand : Fold"
            />
        </div>
    </div>
</template>

<script>
import { ref } from 'vue'
import {
    Expand,
    Fold,
    Location,
    Menu as IconMenu,
    Document,
    Setting, DataLine, DataAnalysis, Box, Key, OfficeBuilding
} from '@element-plus/icons-vue'

export default {
    name: "SidebarMenu",
    components: {OfficeBuilding, Key, Box, DataAnalysis, DataLine, Location, IconMenu, Document, Setting },
    data() {
        return {}
    },
    methods: {
        toPage(path) {
            console.log(path)
            this.$router.push(path)
        }
    },
    setup() {
        const isCollapse = ref(false)
        const handleOpen = (key, keyPath) => {
            console.log(key, keyPath)
        }
        const handleClose = (key, keyPath) => {
            console.log(key, keyPath)
        }
        const toggleCollapse = () => {
            isCollapse.value = !isCollapse.value
        }
        return { isCollapse, handleOpen, handleClose, toggleCollapse, Expand, Fold }
    }
}
</script>

<style scoped>
.menu-item-text{
    font-weight: bolder;
}
.sidebar {
    height: 100vh;
    display: flex;
    flex-direction: column;
    border-right: 1px solid #e6e6e6;
    transition: all 0.3s;
    width: 200px;
    position: relative;
}

.sidebar.is-collapsed {
    width: 64px;
}

.logo, .avatar {
    height: 60px;
    display: flex;
    justify-content: center;
    align-items: center;
    flex-shrink: 0;
}

.collapse-btn-container {
    position: absolute;
    right: -15px;
    top: 50%;
    transform: translateY(-50%);
    z-index: 1000;
}

.collapse-btn {
    padding: 8px;
    border: none;
    background-color: #fff;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.12);
    border-radius: 50%;
    cursor: pointer;
}

.sidebar-menu {
    flex: 1;
    overflow-y: auto;
    border-right: none;
}

/* 自定义菜单项样式 */
.el-menu-item,
.el-sub-menu__title {
    height: 50px;
    line-height: 50px;
}

.el-menu-item-group__title {
    padding: 7px 0 7px 20px;
    line-height: normal;
    font-size: 12px;
    color: #909399;
}
</style>
