import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
// import AutoImport from 'unplugin-auto-import/vite'
// import Components from 'unplugin-vue-components/vite'
// import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'
// import ElementPlus from "unplugin-element-plus/vite";
import vueDevTools from 'vite-plugin-vue-devtools'
// import ElementPlus from 'unplugin-element-plus/vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
    // ElementPlus({
    //   useSource: true,
    // }),
    // AutoImport({
    //   resolvers: [ElementPlusResolver({importStyle: 'sass'})],
    // }),
    // Components({
    //   resolvers: [ElementPlusResolver({importStyle: 'sass'})],
    // }),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path
      }
    }
  },
  // css: {
  //   preprocessorOptions: {
  //     scss: {
  //       additionalData: '@use "@/assets/index.scss" as *;',
  //     },
  //   },
  // },
})