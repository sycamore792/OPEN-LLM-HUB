<template>
    <router-view v-slot="{ Component }">
        <transition  name="fade-slide">
            <component :is="Component" />
        </transition>
    </router-view>
</template>


<style lang="less">
*{
    box-sizing: border-box;
    body{
        margin: 0;
    }
}
html,body{
    width: 100%;
    height: 100%;
}

::-webkit-scrollbar {
    background-color: transparent;
    width: 5px;
    height: 5px;
}
::-webkit-scrollbar-thumb {
    background-color: rgba(0, 0, 0, 0.25);
    border-radius: 5px;
}

#app {

  width: 100%;
  height: 100%;
}
.fade-slide-enter-active {
  transition: all 0.5s;
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateX(-30px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateX(30px);
}
</style>
<script setup>
</script>
