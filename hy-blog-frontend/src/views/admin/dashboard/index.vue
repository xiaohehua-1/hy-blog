<template>
  <div class="dashboard-container">
    <el-row :gutter="20" class="panel-group">
      <el-col :xs="12" :sm="12" :lg="4" v-for="(item, index) in statList" :key="index" class="card-panel-col">
        <div class="card-panel">
          <div class="card-panel-icon-wrapper" :class="item.color">
            <el-icon class="card-panel-icon"><component :is="item.icon" /></el-icon>
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">{{ item.label }}</div>
            <div class="card-panel-num">{{ item.value }}</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :xs="24" :sm="24" :lg="8">
        <el-card shadow="never" class="chart-wrapper">
          <template #header>
            <div class="chart-header">📚 内容构成</div>
          </template>
          <div ref="pieChartRef" style="width: 100%; height: 350px;"></div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="24" :lg="16">
        <el-card shadow="never" class="chart-wrapper">
          <template #header>
            <div class="chart-header-row">
              <span>📈 互动趋势</span>
              <el-radio-group v-model="timeRange" size="small" @change="handleTimeChange">
                <el-radio-button label="week">本周</el-radio-button>
                <el-radio-button label="month">本月</el-radio-button>
                <el-radio-button label="year">全年</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div ref="lineChartRef" style="width: 100%; height: 350px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, reactive } from 'vue'
import request from '@/utils/request'
import * as echarts from 'echarts'
import { Document, PriceTag, Folder, ChatDotRound, Message, View } from '@element-plus/icons-vue'

const pieChartRef = ref(null)
const lineChartRef = ref(null)
let pieChart = null
let lineChart = null

const timeRange = ref('week')
const statList = ref([
  { label: '文章', value: 0, icon: Document, color: 'icon-blue' },
  { label: '标签', value: 0, icon: PriceTag, color: 'icon-green' },
  { label: '分类', value: 0, icon: Folder, color: 'icon-yellow' },
  { label: '评论', value: 0, icon: ChatDotRound, color: 'icon-red' },
  { label: '留言', value: 0, icon: Message, color: 'icon-purple' },
  { label: '访问', value: 0, icon: View, color: 'icon-cyan' }
])

const initPieChart = (data) => {
  if (pieChart) pieChart.dispose()
  pieChart = echarts.init(pieChartRef.value)
  pieChart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: '0%' },
    series: [{
      name: '统计',
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['50%', '45%'],
      avoidLabelOverlap: false,
      itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
      data: [
        { value: data.blogCount, name: '文章' },
        { value: data.tagCount, name: '标签' },
        { value: data.typeCount, name: '分类' }
      ]
    }]
  })
}

const initLineChart = (data) => {
  if (lineChart) lineChart.dispose()
  lineChart = echarts.init(lineChartRef.value)
  lineChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: data.dateList
    },
    yAxis: { 
      type: 'value',
      minInterval: 1, // <--- 关键修改：强制Y轴最小间隔为1，消除小数点
      splitLine: { lineStyle: { type: 'dashed' } } 
    },
    series: [
      {
        name: '评论', type: 'line', smooth: true,
        data: data.commentTrend,
        itemStyle: { color: '#F56C6C' }, areaStyle: { opacity: 0.1 }
      },
      {
        name: '留言', type: 'line', smooth: true,
        data: data.messageTrend,
        itemStyle: { color: '#6F42C1' }, areaStyle: { opacity: 0.1 }
      },
      {
        name: '访问', type: 'line', smooth: true,
        data: data.viewTrend,
        itemStyle: { color: '#00BCD4' }, areaStyle: { opacity: 0.1 }
      }
    ]
  })
}

const fetchData = async () => {
  try {
    const res = await request.get('/admin/dashboard/stats', {
      params: { type: timeRange.value }
    })
    const data = res.data.stats
    
    // 更新卡片数值
    statList.value[0].value = data.blogCount
    statList.value[1].value = data.tagCount
    statList.value[2].value = data.typeCount
    statList.value[3].value = data.commentCount
    statList.value[4].value = data.messageCount
    statList.value[5].value = data.viewCount
    
    initPieChart(data)
    initLineChart(data)
  } catch (e) {
    console.error(e)
  }
}

const handleTimeChange = () => {
  fetchData()
}

const resizeHandler = () => {
  pieChart?.resize()
  lineChart?.resize()
}

onMounted(() => {
  fetchData()
  window.addEventListener('resize', resizeHandler)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeHandler)
  pieChart?.dispose()
  lineChart?.dispose()
})
</script>

<style scoped>
.dashboard-container { padding: 20px; background-color: #f0f2f5; min-height: 100vh; }
.panel-group { margin-bottom: 20px; }
.card-panel-col { margin-bottom: 20px; }

.card-panel {
  height: 108px;
  background: #fff;
  border-radius: 4px; /* 简单的圆角 */
  box-shadow: 0 1px 4px rgba(0,21,41,0.08); /* 轻微阴影 */
  display: flex;
  align-items: center;
  padding: 0 20px; /* 内部留白 */
}

.card-panel-icon-wrapper {
  padding: 16px;
  border-radius: 6px;
  transition: all 0.3s;
}

/* 鼠标悬停时图标背景变色 */
.card-panel:hover .card-panel-icon-wrapper {
  color: #fff;
}
.card-panel:hover .icon-blue { background: #36a3f7; }
.card-panel:hover .icon-green { background: #34bfa3; }
.card-panel:hover .icon-yellow { background: #ffba00; }
.card-panel:hover .icon-red { background: #f56c6c; }
.card-panel:hover .icon-purple { background: #6f42c1; }
.card-panel:hover .icon-cyan { background: #00bcd4; }

.card-panel-icon { float: left; font-size: 48px; }

.card-panel-description {
  margin-left: 20px;
  font-weight: bold;
}

.card-panel-text {
  line-height: 18px;
  color: rgba(0, 0, 0, 0.45);
  font-size: 16px;
  margin-bottom: 12px;
}

.card-panel-num { font-size: 20px; color: #666; }

/* 颜色定义 */
.icon-blue { color: #36a3f7; }
.icon-green { color: #34bfa3; }
.icon-yellow { color: #ffba00; }
.icon-red { color: #f56c6c; }
.icon-purple { color: #6f42c1; }
.icon-cyan { color: #00bcd4; }

.chart-wrapper {
  background: #fff;
  margin-bottom: 20px;
}

.chart-header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>