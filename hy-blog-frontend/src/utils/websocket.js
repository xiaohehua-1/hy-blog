/**
 * WebSocket 封装工具类
 */
export default class SocketService {
  /**
   * 单例
   */
  static instance = null;
  static get getInstance() {
    if (!this.instance) {
      this.instance = new SocketService();
    }
    return this.instance;
  }

  // 和服务端连接的socket对象
  ws = null;

  // 业务类型和对应的回调函数集合
  callBackMapping = {};

  // 标识是否连接成功
  connected = false;

  // 记录重试次数
  sendRetryCount = 0;

  // 重新连接尝试次数
  reconnectCount = 0;

  // 定义连接服务器的方法
  connect() {
    // 连接服务器
    if (!window.WebSocket) {
      return console.log('您的浏览器不支持WebSocket');
    }
    
    // 获取当前协议 (ws 或 wss)
    const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
    // 通过 Vite 代理连接
    const wsUrl = `${protocol}//${window.location.host}/websocket/interaction`;

    this.ws = new WebSocket(wsUrl);

    // 连接成功的事件
    this.ws.onopen = () => {
      console.log('连接服务端成功');
      this.connected = true;
      // 重置重连次数
      this.reconnectCount = 0;
    };

    // 1.连接服务端失败的事件
    // 2.当连接成功之后, 服务端关闭的情况
    this.ws.onclose = () => {
      console.log('连接服务端失败');
      this.connected = false;
      // 重连
      this.reconnectCount++;
      setTimeout(() => {
        this.connect();
      }, this.reconnectCount * 1000);
    };

    // 得到服务端发送过来的数据
    this.ws.onmessage = (msg) => {
      console.log('从服务端接收到的数据:', msg.data);
      // 直接触发所有的回调 (在这个简单的博客场景下，广播即刷新)
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
