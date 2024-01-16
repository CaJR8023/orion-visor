import type { UnwrapRef } from 'vue';
import type { TerminalPreference } from '@/store/modules/terminal/types';
import type { ITerminalChannel, ITerminalSession, ITerminalSessionHandler, TerminalAddons, TerminalDomRef } from '../types/terminal.type';
import { useTerminalStore } from '@/store';
import { InputProtocol } from '../types/terminal.protocol';
import { fontFamilySuffix, TerminalStatus } from '../types/terminal.const';
import { Terminal } from 'xterm';
import { FitAddon } from 'xterm-addon-fit';
import { WebLinksAddon } from 'xterm-addon-web-links';
import { ISearchOptions, SearchAddon } from 'xterm-addon-search';
import { ImageAddon } from 'xterm-addon-image';
import { CanvasAddon } from 'xterm-addon-canvas';
import { WebglAddon } from 'xterm-addon-webgl';
import { playBell } from '@/utils/bell';
import TerminalSessionHandler from './terminal-session-handler';
import { addEventListen } from '@/utils/event';

// 终端会话实现
export default class TerminalSession implements ITerminalSession {

  public readonly hostId: number;

  public sessionId: string;

  public inst: Terminal;

  public connected: boolean;

  public canWrite: boolean;

  public status: number;

  public handler: ITerminalSessionHandler;

  private readonly channel: ITerminalChannel;

  private readonly addons: TerminalAddons;

  constructor(hostId: number,
              sessionId: string,
              channel: ITerminalChannel) {
    this.hostId = hostId;
    this.sessionId = sessionId;
    this.channel = channel;
    this.connected = false;
    this.canWrite = false;
    this.status = TerminalStatus.CONNECTING;
    this.inst = undefined as unknown as Terminal;
    this.handler = undefined as unknown as ITerminalSessionHandler;
    this.addons = {} as TerminalAddons;
  }

  // 初始化
  init(domRef: TerminalDomRef): void {
    const { preference } = useTerminalStore();
    // 初始化实例
    this.inst = new Terminal({
      ...(preference.displaySetting as any),
      theme: preference.theme.schema,
      fastScrollModifier: !!preference.interactSetting.fastScrollModifier ? 'alt' : 'none',
      altClickMovesCursor: !!preference.interactSetting.altClickMovesCursor,
      rightClickSelectsWord: !!preference.interactSetting.rightClickSelectsWord,
      fontFamily: preference.displaySetting.fontFamily + fontFamilySuffix,
      wordSeparator: preference.interactSetting.wordSeparator,
      scrollback: preference.sessionSetting.scrollBackLine,
    });
    // 处理器
    this.handler = new TerminalSessionHandler(this, domRef);
    // 注册快捷键
    this.registerShortcut(preference);
    // 注册事件
    this.registerEvent(domRef.el, preference);
    // 注册插件
    this.registerAddons(preference);
    // 打开终端
    this.inst.open(domRef.el);
    // 自适应
    this.addons.fit.fit();
  }

  // 注册快捷键
  private registerShortcut(preference: UnwrapRef<TerminalPreference>) {
    // 处理自定义按键
    this.inst.attachCustomKeyEventHandler((e: KeyboardEvent) => {
      e.preventDefault();
      // 触发快捷键检测
      if (e.type === 'keydown'
        && preference.shortcutSetting.enabled
        && preference.shortcutSetting.keys.length) {
        // 触发快捷键
        return this.handler.triggerShortcutKey(e);
      }
      return true;
    });
  }

  // 注册事件
  private registerEvent(dom: HTMLElement, preference: UnwrapRef<TerminalPreference>) {
    // 注册输入事件
    this.inst.onData(s => {
      if (!this.canWrite || !this.connected) {
        return;
      }
      // 输入
      this.channel.send(InputProtocol.INPUT, {
        sessionId: this.sessionId,
        command: s
      });
    });
    // 启用响铃
    if (preference.interactSetting.enableBell) {
      this.inst.onBell(() => {
        // 播放蜂鸣
        playBell();
      });
    }
    // 选中复制
    if (preference.interactSetting.selectionChangeCopy) {
      this.inst.onSelectionChange(() => {
        // 复制选中内容
        this.handler.copy();
      });
    }
    // 注册 resize 事件
    this.inst.onResize(({ cols, rows }) => {
      if (!this.connected) {
        return;
      }
      this.channel.send(InputProtocol.RESIZE, {
        sessionId: this.sessionId,
        cols,
        rows
      });
    });
    // 设置右键选项
    addEventListen(dom, 'contextmenu', async () => {
      // 右键粘贴逻辑
      if (preference.interactSetting.rightClickPaste) {
        if (!this.canWrite || !this.connected) {
          return;
        }
        // 未开启右键选中 || 开启并无选中的内容则粘贴
        if (!preference.interactSetting.rightClickSelectsWord || !this.inst.hasSelection()) {
          this.handler.paste();
        }
      }
    });
  }

  // 注册插件
  private registerAddons(preference: UnwrapRef<TerminalPreference>) {
    this.addons.fit = new FitAddon();
    this.addons.search = new SearchAddon();
    // 超链接插件
    if (preference.pluginsSetting.enableWeblinkPlugin) {
      this.addons.weblink = new WebLinksAddon();
    }
    if (preference.pluginsSetting.enableWebglPlugin) {
      // WebGL 渲染插件
      this.addons.webgl = new WebglAddon();
    } else {
      // canvas 渲染插件
      this.addons.canvas = new CanvasAddon();
    }
    // 图片渲染插件
    if (preference.pluginsSetting.enableImagePlugin) {
      this.addons.image = new ImageAddon();
    }
    // 加载插件
    for (const addon of Object.values(this.addons)) {
      this.inst.loadAddon(addon);
    }
  }

  // 设置已连接
  connect(): void {
    this.status = TerminalStatus.CONNECTED;
    this.connected = true;
    this.inst.focus();
  }

  // 设置是否可写
  setCanWrite(canWrite: boolean): void {
    this.canWrite = canWrite;
    if (canWrite) {
      this.inst.options.cursorBlink = useTerminalStore().preference.displaySetting.cursorBlink;
    } else {
      this.inst.options.cursorBlink = false;
    }
  }

  // 写入数据
  write(value: string | Uint8Array): void {
    this.inst.write(value);
  }

  // 聚焦
  focus(): void {
    this.inst.focus();
  }

  // 失焦
  blur(): void {
    this.inst.blur();
  }

  // 自适应
  fit(): void {
    this.addons.fit?.fit();
  }

  // 查找
  find(word: string, next: boolean, options: ISearchOptions): void {
    if (next) {
      this.addons.search.findNext(word, options);
    } else {
      this.addons.search.findPrevious(word, options);
    }
  }

  // 断开连接
  disconnect(): void {
    // 发送关闭消息
    this.channel.send(InputProtocol.CLOSE, {
      sessionId: this.sessionId
    });
  }

  // 关闭
  close(): void {
    try {
      // 卸载插件
      Object.values(this.addons)
        .filter(Boolean)
        .forEach(s => s.dispose());
      // 卸载终端
      this.inst.dispose();
    } catch (e) {
    }
  }

}
