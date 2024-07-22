module.exports = {
	devServer: {
		host: "0.0.0.0",
		port: 40, // 端口号
		https: false, // https:{type:Boolean}
		open: false, //配置自动启动浏览器
		proxy: {
			"/apis": {
				target: "http://91.69.199.39:8080/", //  本地
				changeOrigin: true,
				pathRewrite: {
					'^/apis': ''
				}
			},
		},
	},
	// // 构建输入文件夹
	// outputDir: process.env.NODE_ENV == 'web' ? 'search' : 'dist',
	//
	// css: {
	// 	// 是否使用css扩展
	// 	requireModuleExtension: true,
	// 	//   loaderOptions: { // 向 CSS 相关的 loader 传递选项
	// 	//     less: {
	// 	//         javascriptEnabled: true
	// 	//     }
	// 	//  }
	// },
	// productionSourceMap: false,
	// // publicPath: process.env.NODE_ENV === 'production' ?
	// //   '../' : './',
	// // publicPath: process.env.NODE_ENV === 'production' ? '../search' : './',
	// publicPath: './',
	// // configureWebpack: {
	// //   externals: {
	// //     'vue': 'Vue',
	// //     'vuex': 'Vuex',
	// //     'vue-router': 'VueRouter',
	// //     'element-ui': 'ELEMENT',
	// //     'axios': 'axios'
	// //   }
	// // }
	// runtimeCompiler: true,
	// chainWebpack: config => {
	// 	config
	// 		.plugin('html')
	// 		.tap(args => {
	// 			args[0].title = '零点一号'
	// 			return args
	// 		});
	// 	config.when(process.env.NODE_ENV === "production", (config) => {
	// 		config.optimization.splitChunks({
	// 			chunks: "all", //initial同步，async异步，all同步或者异步
	// 			automaticNameDelimiter: "-",
	// 			cacheGroups: {
	// 				"element-ui": {
	// 					name: "chunk-elementUI",
	// 					priority: -10,
	// 					test: /[\\/]node_modules[\\/]element-ui[\\/]/,
	// 					minSize: 0
	// 				},
	// 				"brace": {
	// 					name: "chunk-brace",
	// 					priority: -10,
	// 					test: /[\\/]node_modules[\\/]brace[\\/]/,
	// 					minSize: 0
	// 				},
	// 				"luckyexcel": {
	// 					name: "chunk-luckyexcel",
	// 					priority: -10,
	// 					test: /[\\/]node_modules[\\/]luckyexcel[\\/]/,
	// 					minSize: 0
	// 				},
	// 				"exceljs": {
	// 					name: "chunk-exceljs",
	// 					priority: -10,
	// 					test: /[\\/]node_modules[\\/]exceljs[\\/]/,
	// 					minSize: 0
	// 				},
	// 				'vendors': {
	// 					name: 'vendors',
	// 					test: /[\\/]node_modules[\\/]/,
	// 					priority: -20
	// 				}
	// 			},
	// 		})
	// 	});
	// },
	// configureWebpack: config => {
	//
	// }

}

