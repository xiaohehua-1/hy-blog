/**
 * WebSocket 单例封装
 * 自动判断 ws/wss 协议，支持断线重连（递增延迟）、发送重试、回调注册/注销
 * 适用于博客站内消息推送等轻量实时场景
 */
export default class SocketService {
  /** 单例 */
  static instance = null;
  static get getInstance() {
    if (!this.instance) {
      this.instance = new SocketService();
    }
    return this.instance;
  }

  ws = null;                // WebSocket 实例
  callBackMapping = {};     // 回调函数映射表
  connected = false;        // 连接状态
  sendRetryCount = 0;       // 发送重试计数（递增延迟）
  reconnectCount = 0;       // 断线重连计数（递增延迟）

  /**
   * 建立 WebSocket 连接，自动适配 wss:///ws://
   * 断线时递增重连延迟（1s / 2s / 3s ...）避免频繁请求
   */
  connect() {
    // 连接服务器
    if (!window.WebSocket) {
      return console.log('您的浏览器不支持WebSocket');
    }
    
    // 自动匹配 ws/wss 协议，经 Vite 代理转发到后端
    const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
    const wsUrl = `${protocol}//${window.location.host}/websocket/interaction`;

    this.ws = new WebSocket(wsUrl);

    this.ws.onopen = () => {
      console.log('连接服务端成功');
      this.connected = true;
      this.reconnectCount = 0; // 连接成功后重置重连计数
    };

    // 连接失败或服务端主动关闭时自动重连，延迟递增（1s / 2s / 3s ...）
    this.ws.onclose = () => {
      console.log('连接服务端失败');
      this.connected = false;
      this.reconnectCount++;
      setTimeout(() => {
        this.connect();
      }, this.reconnectCount * 1000);
    };

    // 收到消息后广播给所有注册的回调（博客场景下简单全量通知即可）
    this.ws.onmessage = (msg) => {
      console.log('从服务端接收到的数据:', msg.data);
      Object.keys(this.callBackMapping).forEach((key) => {
        this.callBackMapping[key](msg.data);
      });
    };
  }

  /**
   * 注册回调函数
   * @param {string} key 唯一标识
   * @param {function} callback 回调函数
   */
  registerCallBack(key, callback) {
    this.callBackMapping[key] = callback;
  }

  /**
   * 取消注册回调函数
   * @param {string} key 唯一标识
   */
  unRegisterCallBack(key) {
    this.callBackMapping[key] = null;
    delete this.callBackMapping[key];
  }

  /**
   * 发送数据的方法
   * @param {any} data 数据
   */
  send(data) {
    // 判断此时此时有没有连接成功
    if (this.connected) {
      this.sendRetryCount = 0;
      this.ws.send(JSON.stringify(data));
    } else {
      this.sendRetryCount++;
      setTimeout(() => {
        this.send(data);
      }, this.sendRetryCount * 500);
    }
  }
}
