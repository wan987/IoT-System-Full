<template>
  <div class="home-container">
    <el-row :gutter="24" style="margin-bottom: 30px;">

      <el-col :span="6">
        <div class="stat-card blue-gradient" @click="$router.push('/station')">
          <div class="card-content">
            <div class="stat-title">监测站点总数</div>
            <div class="stat-value">{{ stats.stationCount }}</div>
          </div>
          <el-icon class="card-icon-bg"><OfficeBuilding /></el-icon>
        </div>
      </el-col>

      <el-col :span="6">
        <div class="stat-card green-gradient" @click="$router.push('/gateway')">
          <div class="card-content">
            <div class="stat-title">在线网关数量</div>
            <div class="stat-value">{{ stats.gatewayCount }}</div>
          </div>
          <el-icon class="card-icon-bg"><Connection /></el-icon>
        </div>
      </el-col>

      <el-col :span="6">
        <div class="stat-card purple-gradient" @click="$router.push('/sensor')">
          <div class="card-content">
            <div class="stat-title">部署传感器</div>
            <div class="stat-value">{{ stats.sensorCount }}</div>
          </div>
          <el-icon class="card-icon-bg"><Cpu /></el-icon>
        </div>
      </el-col>

      <el-col :span="6">
        <div class="stat-card red-gradient" @click="$router.push('/data')">
          <div class="card-content">
            <div class="stat-title">今日报警次数</div>
            <div class="stat-value">{{ stats.alarmCount }}</div>
          </div>
          <el-icon class="card-icon-bg"><Bell /></el-icon>
        </div>
      </el-col>
    </el-row>

    <el-card shadow="always" class="chart-card">
      <template #header>
        <div class="card-header">
          <span class="header-title">
            <el-icon style="margin-right: 8px; top: 2px; position: relative;"><TrendCharts /></el-icon>
            全局数据监控趋势
          </span>
          <el-tag type="info" effect="plain">实时</el-tag>
        </div>
      </template>
      <div id="homeChart" style="width: 100%; height: 420px;"></div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import axios from 'axios'
import * as echarts from 'echarts'
import { OfficeBuilding, Connection, Cpu, Bell, TrendCharts } from '@element-plus/icons-vue'

const stats = ref({ stationCount: 0, gatewayCount: 0, sensorCount: 0, alarmCount: 0 })
let myChart = null

const loadStats = () => {
  axios.get('http://localhost:8080/stat/summary').then(res => {
    if (res.data.code === 200) stats.value = res.data.data
  })
}

const loadChartData = () => {
  axios.get(`http://localhost:8080/data/page?pageNum=1&pageSize=50`).then(res => {
    if (res.data.code === 200) {
      // 简单的排序逻辑
      const sorted = res.data.data.records.reverse()
      const xData = sorted.map(item => item.dataTime)
      const yData = sorted.map(item => item.value)
      initChart(xData, yData)
    }
  })
}

const initChart = (xData, yData) => {
  const chartDom = document.getElementById('homeChart')
  myChart = echarts.init(chartDom)

  // ECharts 美化配置
  const option = {
    backgroundColor: '#fff', // 背景色
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255, 255, 255, 0.9)',
      borderColor: '#409EFF',
      textStyle: { color: '#333' }
    },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: xData,
      axisLine: { lineStyle: { color: '#ccc' } },
      axisLabel: { color: '#666' }
    },
    yAxis: {
      type: 'value',
      splitLine: { lineStyle: { type: 'dashed', color: '#eee' } } // 虚线网格
    },
    series: [
      {
        name: '数值',
        type: 'line',
        smooth: true, // 平滑曲线
        symbol: 'none', // 去掉小圆点，只要线
        lineStyle: { width: 3, color: '#409EFF' }, // 线条加粗
        areaStyle: {
          // 核心：青色渐变填充
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64, 158, 255, 0.4)' },
            { offset: 1, color: 'rgba(64, 158, 255, 0.01)' }
          ])
        },
        data: yData
      }
    ]
  }
  myChart.setOption(option)
}

onMounted(() => {
  loadStats()
  nextTick(() => loadChartData())
  window.addEventListener('resize', () => myChart && myChart.resize())
})
</script>

<style scoped>
.home-container {
  padding: 20px;
  background-color: #f0f2f5; /* 浅灰背景，凸显卡片 */
  min-height: calc(100vh - 80px);
}

/* --- 卡片通用样式 --- */
.stat-card {
  height: 120px;
  border-radius: 12px;
  color: white;
  padding: 20px 24px;
  cursor: pointer;
  position: relative;
  overflow: hidden; /* 隐藏溢出的图标水印 */
  box-shadow: 0 6px 16px rgba(0,0,0,0.1);
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
}

/* 鼠标悬停动效 */
.stat-card:hover {
  transform: translateY(-5px); /* 向上浮动 */
  box-shadow: 0 12px 24px rgba(0,0,0,0.2);
}

.card-content {
  z-index: 2; /* 保证文字在图标上面 */
}

.stat-title {
  font-size: 14px;
  opacity: 0.9;
  margin-bottom: 8px;
  letter-spacing: 1px;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  font-family: 'Helvetica Neue', sans-serif;
}

/* --- 水印大图标样式 --- */
.card-icon-bg {
  position: absolute;
  right: -10px;
  bottom: -10px;
  font-size: 90px; /* 超大图标 */
  opacity: 0.15; /* 半透明 */
  transform: rotate(-15deg); /* 稍微倾斜 */
  z-index: 1;
  transition: all 0.3s;
}

.stat-card:hover .card-icon-bg {
  right: 0; /* 悬停时图标稍微动一下 */
  opacity: 0.25;
}

/* --- 四种渐变色 (CSS的核心魔法) --- */
.blue-gradient {
  background: linear-gradient(135deg, #36d1dc 0%, #5b86e5 100%);
}
.green-gradient {
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
}
.purple-gradient {
  background: linear-gradient(135deg, #c471f5 0%, #fa71cd 100%);
}
.red-gradient {
  background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 99%, #fecfef 100%);
  /* 或者用更有警示感的 */
  background: linear-gradient(135deg, #ff512f 0%, #dd2476 100%);
}

/* --- 图表卡片 --- */
.chart-card {
  border-radius: 12px;
  border: none;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.05);
}
.header-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>