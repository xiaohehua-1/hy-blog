<template>
  <div class="card-panel" @click="handleCardClick">
    <div class="card-panel-icon-wrapper" :class="color">
      <el-icon class="card-panel-icon"><component :is="icon" /></el-icon>
    </div>
    <div class="card-panel-description">
      <div class="card-panel-text">{{ label }}</div>
      <div class="card-panel-num">{{ value }}</div>
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue'

const props = defineProps({
  icon: {
    type: Object, // Element Plus Icon component
    required: true
  },
  label: {
    type: String,
    required: true
  },
  value: {
    type: [String, Number],
    required: true
  },
  color: {
    type: String,
    default: 'blue' // Default color class
  }
})

const emit = defineEmits(['click'])

const handleCardClick = () => {
  emit('click')
}
</script>

<style lang="scss" scoped>
.card-panel-col {
  margin-bottom: 20px;
}

.card-panel {
  height: 108px;
  cursor: pointer;
  font-size: 12px;
  position: relative;
  overflow: hidden;
  color: #666;
  background: #fff;
  box-shadow: 4px 4px 40px rgba(0, 0, 0, .05);
  border-color: rgba(0, 0, 0, .05);
  border-radius: 8px;
  display: flex;
  align-items: center;
  padding: 0 16px;

  &:hover {
    .card-panel-icon-wrapper {
      color: #fff; // 鼠标悬停时图标颜色变为白色
      // 鼠标悬停时，根据颜色类设置背景色
      &.blue {
        background: #36a3f7; // 与蓝色图标对应
      }
      &.green {
        background: #34bfa3; // 与绿色图标对应
      }
      &.red {
        background: #f4516c; // 与红色图标对应
      }
      &.yellow {
        background: #fbd431; // 与黄色图标对应
      }
      &.purple {
        background: #6F42C1; // 自定义紫色
      }
      &.cyan {
        background: #00BCD4; // 自定义青色
      }
      &.orange { // 橙色悬停背景
        background: #FFA500;
      }
      &.pink { // 粉色悬停背景
        background: #FF69B4;
      }
    }
  }

  .card-panel-icon-wrapper {
    transition: all 0.38s ease-out;
    border-radius: 6px;
    padding: 16px;
    margin-right: 14px;
    display: flex;
    align-items: center;
    justify-content: center;

    &.green {
      color: #34bfa3;
    }
    &.blue {
      color: #36a3f7;
    }
    &.red {
      color: #f4516c;
    }
    &.yellow {
      color: #fbd431;
    }
    &.purple { // 紫色样式
      color: #6F42C1;
    }
    &.cyan { // 青色样式
      color: #00BCD4;
    }
    &.orange { // 橙色样式
      color: #FFA500; // 使用一个默认的橙色值
    }
    &.pink { // 粉色样式
      color: #FF69B4; // 使用一个默认的粉色值
    }
  }

  .card-panel-icon {
    font-size: 48px;
  }

  .card-panel-description {
    font-weight: bold;
    margin: 26px;
    margin-left: 0px;

    .card-panel-text {
      line-height: 18px;
      color: rgba(0, 0, 0, 0.45);
      font-size: 16px;
      margin-bottom: 12px;
    }

    .card-panel-num {
      font-size: 20px;
    }
  }
}
</style>
