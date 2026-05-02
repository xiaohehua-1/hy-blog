import { createApp } from 'vue'
import App from './App.vue'

// 1. Element Plus
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

// 2. Markdown 编辑器 (v-md-editor)
import VMdEditor from '@kangc/v-md-editor';
import '@kangc/v-md-editor/lib/style/base-editor.css';
import githubTheme from '@kangc/v-md-editor/lib/theme/github.js';
import '@kangc/v-md-editor/lib/theme/style/github.css';

// 引入 highlight.js (代码高亮)
import hljs from 'highlight.js';

// 3. 路由
import router from './router'

// 引入字体样式
import '@/assets/styles/fonts.css'
// 引入全局样式
import '@/assets/styles/theme.css' 

// 配置编辑器主题
VMdEditor.use(githubTheme, {
  Hljs: hljs,
});

const app = createApp(App)

// 注册图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(ElementPlus, { locale: zhCn })
app.use(VMdEditor) // 注册编辑器
app.use(router)

app.mount('#app')