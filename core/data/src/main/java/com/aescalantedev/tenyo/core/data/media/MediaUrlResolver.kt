package com.aescalantedev.tenyo.core.data.media

/**
 * Resolutor centralizado de URLs multimedia para imagenes miniatura y animaciones GIF.
 *
 * En la base de datos local (Room) y en la futura base de datos remota (Supabase PostgreSQL),
 * unicamente se almacenan rutas relativas como `images/0001-2gPfomN.jpg` o `videos/0001-2gPfomN.gif`.
 *
 * Esta clase construye la URL completa en tiempo de ejecucion. Actualmente apunta al CDN Raw de GitHub
 * del dataset oficial, pero permite migrar a un bucket de AWS S3, Cloudflare R2 o Supabase Storage
 * cambiando unicamente [baseUrl] sin requerir migraciones de datos en SQLite ni cambios en la UI.
 */
class MediaUrlResolver(
    private val baseUrl: String = DEFAULT_GITHUB_RAW_BASE_URL
) {
    /**
     * Resuelve una ruta relativa de imagen o GIF en una URL absoluta lista para ser consumida por Coil 3.
     *
     * @param relativePath Ruta relativa almacenada en la entidad, por ejemplo `images/0025-xyz.jpg`.
     * @return URL absoluta o cadena vacia si la ruta relativa esta en blanco.
     */
    fun resolve(relativePath: String): String {
        val cleanPath = relativePath.trim()
        if (cleanPath.isBlank()) return ""
        if (cleanPath.startsWith("http://") || cleanPath.startsWith("https://")) {
            return cleanPath
        }
        val normalizedBase = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        val normalizedRelative = cleanPath.removePrefix("/")
        return "$normalizedBase$normalizedRelative"
    }

    companion object {
        const val DEFAULT_GITHUB_RAW_BASE_URL =
            "https://raw.githubusercontent.com/hasaneyldrm/exercises-dataset/main/"
    }
}
