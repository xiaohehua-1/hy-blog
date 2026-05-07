package com.heyi.blog.utils;

import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.ext.heading.anchor.HeadingAnchorExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Markdown 转 HTML 工具类
 *
 * 将 Markdown 文本解析为 HTML，并通过 Jsoup 白名单机制过滤 XSS 攻击向量，
 * 同时支持 GFM 表格、标题锚点等扩展语法。
 *
 * 核心依赖：commonmark-java（Markdown 解析）、jsoup（HTML 安全过滤）
 * 使用场景：博客文章内容渲染、评论内容展示
 */
public class MarkdownUtils {

    /**
     * 基础转换：Markdown → HTML → 安全过滤，不含 GFM 扩展
     *
     * @param markdown 原始 Markdown 文本
     * @return 过滤后的安全 HTML，null 输入返回空字符串
     */
    public static String markdownToHtml(String markdown) {
        // 空值保护：避免 NPE，返回空串兼容前端展示
        if (markdown == null) return "";
        // commonmark 默认解析器，不支持表格等扩展
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        String content = renderer.render(document);
        // 输出前统一做 XSS 清洗
        return cleanHtml(content);
    }

    /**
     * 扩展转换：支持 GFM 表格和标题锚点，输出安全 HTML
     *
     * @param markdown 原始 Markdown 文本
     * @return 带锚点和表格支持的安全 HTML，null 输入返回空字符串
     */
    public static String markdownToHtmlExtensions(String markdown) {
        // 空值保护：避免 NPE
        if (markdown == null) return "";
        // 标题锚点扩展：为 h1-h6 自动生成 id，支持页面内锚点跳转
        Set<Extension> headingAnchorExtensions = Collections.singleton(HeadingAnchorExtension.create());
        // GFM 表格扩展：解析 Markdown 表格语法为 HTML <table>
        List<Extension> tableExtension = Collections.singletonList(TablesExtension.create());
        // 解析阶段注册表格扩展
        Parser parser = Parser.builder()
                .extensions(tableExtension)
                .build();
        Node document = parser.parse(markdown);
        // 渲染阶段同时注册标题锚点和表格扩展
        HtmlRenderer renderer = HtmlRenderer.builder()
                .extensions(headingAnchorExtensions)
                .extensions(tableExtension)
                .build();
        String content = renderer.render(document);
        // 输出前统一做 XSS 清洗
        return cleanHtml(content);
    }

    /**
     * Jsoup 白名单过滤，防御 XSS 攻击
     *
     * 使用 basicWithImages 白名单作为基础，额外放开 class/style/id 属性
     * 和 Markdown 常用块级标签，同时过滤 script/style/onclick 等危险内容。
     */
    private static String cleanHtml(String content) {
        // basicWithImages 白名单：允许基础格式标签 + <img>，默认禁止 script/onclick 等
        // 补充 class/id/style 属性以支持前端样式，补充 div/span/h1-h6 兼容 Markdown 渲染产物
        Safelist safelist = Safelist.basicWithImages()
                .addAttributes(":all", "class", "style", "id")
                .addTags("h1", "h2", "h3", "h4", "h5", "h6", "hr", "span", "div");
        // prettyPrint(false)：不美化输出，避免引入多余空白文本节点
        return Jsoup.clean(content, "", safelist, new Document.OutputSettings().prettyPrint(false));
    }
}