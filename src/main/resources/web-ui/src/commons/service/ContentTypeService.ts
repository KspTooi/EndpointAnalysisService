
export default {

    /**
     * 根据contentType获取ContentType
     * @param contentType 
     * @returns 
     */
    getContentType(contentType: string | null) : ContentType {

        if(contentType === null || contentType === undefined || contentType === ''){
            return ContentType.TEXT
        }
        
        // 处理包含 charset 等参数的情况，提取主要的 content type
        let mainContentType = contentType.trim()
        if (mainContentType.includes(';')) {
            mainContentType = mainContentType.split(';')[0].trim()
        }
        
        // 直接匹配枚举值
        for (const [key, value] of Object.entries(ContentType)) {
            if (value === mainContentType) {
                return ContentType[key as keyof typeof ContentType]
            }
        }
        
        return ContentType.TEXT
    }

}

export enum ContentType {
    JSON = "application/json",
    FORM = "application/x-www-form-urlencoded",
    TEXT = "text/plain",
    XML = "application/xml",
    HTML = "text/html",
    JAVASCRIPT = "application/javascript",
    BINARY = "application/octet-stream",
    MULTIPART = "multipart/form-data"
}