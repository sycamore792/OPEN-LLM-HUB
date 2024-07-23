<template>
    <div class="container mx-auto px-4 py-8">
        <div class="flex flex-wrap justify-center">
            <div v-for="(chart, index) in charts" :key="index" class="w-full md:w-1/2 lg:w-1/3 p-4">
                <div class="bg-white p-4 rounded-lg shadow">
                    <h2 style="padding-left: 50px" class="text-lg font-semibold mb-4">{{ chart.title }}</h2>
                    <div class="flex justify-between items-center mb-4">
                        <!-- 可以根据需要添加控件 -->
                    </div>
                    <div :ref="el => { if (el) chartRefs[index] = el }"
                         style="margin: auto ;width: 800px; height: 400px;padding: 20px"></div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import * as echarts from 'echarts';
import DataApi from "@/api/DataApi";

export default {
    data() {
        return {
            charts: [
                {
                    title: 'API 请求次数',
                    option: {
                        tooltip: {trigger: 'axis'},
                        xAxis: {
                            type: 'category',
                            data: []
                        },
                        yAxis: {type: 'value', name: '次数', min: 0,},
                        series: [{
                            data: [],
                            type: 'bar',
                            itemStyle: {color: '#5853e0'}
                        }]
                    }
                },

                {
                    title: 'Token消耗趋势',
                    option: {
                        tooltip: {trigger: 'axis'},
                        xAxis: {
                            type: 'category',
                            data: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']
                        },
                        yAxis: {type: 'value', name: 'Token消耗量'},
                        series: [{data: [120, 200, 150, 80, 70, 110, 130, 140, 160, 180, 190, 210], type: 'line'}]
                    }
                },
                {
                    title: '模型',
                    option: {

                        // 鼠标移入显示的东西
                        tooltip: {
                            trigger: 'item',
                            formatter: '{a} <br/>{b} : {c} ({d}%)'
                        },
                        legend: {
                            left: 'center',
                            top: 'bottom',
                            data: [
                                'rose1',
                                'rose2',
                                'rose3',
                                'rose4',
                                'rose5',
                                'rose6',
                                'rose7',
                                'rose8'
                            ]
                        },
                        toolbox: {
                            show: false,
                            feature: {
                                mark: {show: true},
                                // 点击小图标数据详情的显示与隐藏
                                dataView: {show: true, readOnly: false},
                                // 刷新图表按键的显示与隐藏
                                restore: {show: true},
                                // 保存图表按键的显示与隐藏
                                saveAsImage: {show: true}
                            }
                        },
                        series: [
                            {
                                // 光标移入显示的东西
                                name: '模型实例推理次数',
                                type: 'pie',
                                radius: [40, 140],
                                center: ['25%', '50%'],
                                roseType: 'radius',
                                itemStyle: {
                                    // 图表圆角尺度
                                    borderRadius: 5
                                },
                                label: {
                                    // 控制图表各属性指示的显示与隐藏
                                    show: false

                                },
                                emphasis: {
                                    label: {
                                        // 鼠标移动到哪个模块 相应指示上数据的隐藏还是显示
                                        show: true
                                    }
                                },
                                data: [
                                    {value: 40, name: 'rose 1'},
                                    {value: 33, name: 'rose 2'},
                                    {value: 28, name: 'rose 3'},
                                    {value: 22, name: 'rose 4'},
                                    {value: 20, name: 'rose 5'},
                                    {value: 15, name: 'rose 6'},
                                    {value: 12, name: 'rose 7'},
                                    {value: 10, name: 'rose 8'}
                                ]
                            },
                        ]
                    }
                }
            ],
            chartInstances: [],
            chartRefs: []
        };
    },
    mounted() {
        this.$nextTick(() => {
            this.fetchAPIRequestData();
        });
        window.addEventListener('resize', this.handleResize);
    },
    methods: {
        initCharts() {
            this.charts.forEach((chart, index) => {
                if (this.chartRefs[index]) {
                    const instance = echarts.init(this.chartRefs[index]);
                    instance.setOption(chart.option);
                    this.chartInstances.push(instance);
                }
            });
        },
        handleResize() {
            this.chartInstances.forEach(chart => {
                chart && chart.resize();
            });
        },
        fetchAPIRequestData() {
            /**
             * {
             *                     title: 'API 请求次数',
             *                     option: {
             *                         tooltip: {trigger: 'axis'},
             *                         xAxis: {
             *                             type: 'category',
             *                             data: []
             *                         },
             *                         yAxis: {type: 'value', name: '次数', min: 0, },
             *                         series: [{
             *                             data: [],
             *                             type: 'bar',
             *                             itemStyle: {color: '#5853e0'}
             *                         }]
             *                     }
             *                 }
             */
            let _this = this;

            DataApi.getApiRequestData().then(res => {
                _this.updateApiRequestData(res.data.data.apiRequestCount)
                _this.updateTokensCostData(res.data.data.apiTokensCost)
                _this.updateModelUsedRequestData(res.data.data.apiModelUsedInfo)

                _this.initCharts();
            })
        },

        updateTokensCostData(data) {

            /**
             * {
             *                     title: 'Token消耗趋势',
             *                     option: {
             *                         tooltip: {trigger: 'axis'},
             *                         xAxis: {
             *                             type: 'category',
             *                             data: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']
             *                         },
             *                         yAxis: {type: 'value', name: 'Token消耗量'},
             *                         series: [{data: [120, 200, 150, 80, 70, 110, 130, 140, 160, 180, 190, 210], type: 'line'}]
             *                     }
             *                 }
             * @type {*[]}
             */

            const xAxisData = [];
            const seriesData = [];

            for (let i = 0; i < data.length; i++) {
                xAxisData.push(data[i].date);
                seriesData.push(data[i].tokensCost);
            }

            this.charts[1].option.xAxis.data = xAxisData;
            this.charts[1].option.series[0].data = seriesData;
        },
        updateApiRequestData(data) {
            const xAxisData = [];
            const seriesData = [];

            for (let i = 0; i < data.length; i++) {
                xAxisData.push(data[i].date);
                seriesData.push(data[i].requestCount);
            }

            this.charts[0].option.xAxis.data = xAxisData;
            this.charts[0].option.series[0].data = seriesData;
        },
        updateModelUsedRequestData(data) {
            /**
             * {
             *
             *                         // 鼠标移入显示的东西
             *                         tooltip: {
             *                             trigger: 'item',
             *                             formatter: '{a} <br/>{b} : {c} ({d}%)'
             *                         },
             *                         legend: {
             *                             left: 'center',
             *                             top: 'bottom',
             *                             data: [
             *                                 'rose1',
             *                                 'rose2',
             *                                 'rose3',
             *                                 'rose4',
             *                                 'rose5',
             *                                 'rose6',
             *                                 'rose7',
             *                                 'rose8'
             *                             ]
             *                         },
             *                         toolbox: {
             *                             show: false,
             *                             feature: {
             *                                 mark: {show: true},
             *                                 // 点击小图标数据详情的显示与隐藏
             *                                 dataView: {show: true, readOnly: false},
             *                                 // 刷新图表按键的显示与隐藏
             *                                 restore: {show: true},
             *                                 // 保存图表按键的显示与隐藏
             *                                 saveAsImage: {show: true}
             *                             }
             *                         },
             *                         series: [
             *                             {
             *                                 // 光标移入显示的东西
             *                                 name: 'Radius Mode 111',
             *                                 type: 'pie',
             *                                 radius: [20, 140],
             *                                 center: ['25%', '50%'],
             *                                 roseType: 'radius',
             *                                 itemStyle: {
             *                                     // 图表圆角尺度
             *                                     borderRadius: 10
             *                                 },
             *                                 label: {
             *                                     // 控制图表各属性指示的显示与隐藏
             *                                     show: true
             *                                 },
             *                                 emphasis: {
             *                                     label: {
             *                                         // 鼠标移动到哪个模块 相应指示上数据的隐藏还是显示
             *                                         show: true
             *                                     }
             *                                 },
             *                                 data: [
             *                                     {value: 40, name: 'rose 1'},
             *                                     {value: 33, name: 'rose 2'},
             *                                     {value: 28, name: 'rose 3'},
             *                                     {value: 22, name: 'rose 4'},
             *                                     {value: 20, name: 'rose 5'},
             *                                     {value: 15, name: 'rose 6'},
             *                                     {value: 12, name: 'rose 7'},
             *                                     {value: 10, name: 'rose 8'}
             *                                 ]
             *                             },
             *                         ]
             *                     }
             * @type {*[]}
             */
            const modelNameList = [];
            const seriesData = [];
            const countList = [];
            const percentList = [];



            for (let i = 0; i < data.length; i++) {
                modelNameList.push(data[i].modelName);
                countList.push(data[i].count);
                percentList.push(data[i].percentage);

                seriesData.push({
                    value: data[i].count,
                    name: data[i].modelName,
                    percent: data[i].percentage
                })
            }

            this.charts[2].option.legend.data = modelNameList;
            this.charts[2].option.series[0].data = seriesData;
            // this.charts[2].option.xAxis.data = xAxisData;
            // this.charts[2].option.series[0].data = seriesData;
        }


    },

    beforeUnmount() {
        this.chartInstances.forEach(chart => {
            if (chart) {
                chart.dispose();
            }
        });
        window.removeEventListener('resize', this.handleResize);
    }
};
</script>
