<template>
    <div class="container mx-auto px-4 py-8">
        <div class="bg-white p-4 rounded-lg shadow mb-6">
            <h2 class="text-lg font-semibold mb-4">每月用量</h2>
            <div class="flex justify-between items-center mb-4">
<!--                <select v-model="selectedMonth" class="border rounded px-2 py-1">-->
<!--                    <option value="2024-07">2024 - 7月</option>-->
<!--                </select>-->
<!--                <button class="bg-blue-500 text-white px-4 py-2 rounded">导出</button>-->
            </div>
            <div ref="chartRef" style="width: 100%; height: 400px;"></div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue';
import * as echarts from 'echarts';

const selectedMonth = ref('2024-07');
const chartRef = ref(null);
let chart = null;

const initChart = () => {
    if (chartRef.value) {
        chart = echarts.init(chartRef.value);
        const option = {
            tooltip: {
                trigger: 'axis'
            },
            xAxis: {
                type: 'category',
                data: ['7-1', '7-2', '7-3', '7-4', '7-5', '7-6', '7-7', '7-8', '7-9', '7-10']
            },
            yAxis: {
                type: 'value',
                name: '消费金额 (CNY)',
                min: 0,
                max: 0.0002,
                interval: 0.00005
            },
            series: [{
                data: [0, 0, 0, 0, 0, 0, 0, 0.0002, 0, 0],
                type: 'bar',
                itemStyle: {
                    color: '#ffa500'
                }
            }]
        };
        chart.setOption(option);
    }
};

const handleResize = () => {
    chart && chart.resize();
};

onMounted(() => {
    initChart();
    window.addEventListener('resize', handleResize);
});

watch(selectedMonth, () => {
    // 这里可以根据选择的月份更新图表数据
    // 示例: updateChartData(selectedMonth.value);
});

onUnmounted(() => {
    if (chart) {
        chart.dispose();
    }
    window.removeEventListener('resize', handleResize);
});
</script>
