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
 * Markdown转HTML工具类 (安全增强版)
 */
public class MarkdownUtils {

    /**
     * markdown格式转换成HTML格式并进行安全过滤
     */
    public static String markdownToHtml(String markdown) {
        if (markdown == null) return "";
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        String content = renderer.render(document);
        return cleanHtml(content);
    }

    /**
     * 增加扩展[标题锚点，表格生成]并进行安全过滤
     */
    public static String markdownToHtmlExtensions(String markdown) {
        if (markdown == null) return "";
        //h标题生成id
        Set<Extension> headingAnchorExtensions = Collections.singleton(HeadingAnchorExtension.create());
        //转换table的HTML
        List<Extension> tableExtension = Collections.singletonList(TablesExtension.create());
        Parser parser = Parser.builder()
                .extensions(tableExtension)
                .build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder()
                .extensions(headingAnchorExtensions)
                .extensions(tableExtension)
                .build();
        String content = renderer.render(document);
        return cleanHtml(content);
    }

    /**
     * 使用 Jsoup 进行 HTML 安全过滤
     */
    private static String cleanHtml(String content) {
        // 使用 basicWithImages 白名单，允许基本格式和图片，但禁止 script、style、onclick 等危险属性
        // 并且允许 class 属性 (方便前端样式展示)
        Safelist safelist = Safelist.basicWithImages()
                .addAttributes(":all", "class", "style", "id") // 允许常用样式属性
                .addTags("h1", "h2", "h3", "h4", "h5", "h6", "hr", "span", "div"); // 补充 Markdown 常用的标题标签
        
        return Jsoup.clean(content, "", safelist, new Document.OutputSettings().prettyPrint(false));
    }
}