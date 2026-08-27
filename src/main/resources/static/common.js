// 公共前端工具：所有页面共享，避免每页重复定义 request / escapeHtml。
// 必须在各页面主 <script> 之前通过 <script src="common.js"></script> 引入。

async function request(url, options = {}) {
    const response = await fetch(url, {
        headers: {"Content-Type": "application/json"},
        ...options
    });
    // 204 No Content（如 DELETE 接口）视为成功且无返回体。
    if (response.status === 204) {
        return null;
    }
    const result = await response.json().catch(() => ({code: 500, message: "解析失败"}));
    if (!response.ok || result.code !== 200) {
        throw new Error(result.message || "请求失败");
    }
    return result.data;
}

function escapeHtml(value) {
    return String(value || "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll("\"", "&quot;")
        .replaceAll("'", "&#039;");
}
