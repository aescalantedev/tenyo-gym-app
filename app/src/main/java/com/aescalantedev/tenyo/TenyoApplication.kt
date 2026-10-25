package com.aescalantedev.tenyo

import android.app.Application
import android.os.Build
import app.rive.runtime.kotlin.core.Rive
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.gif.AnimatedImageDecoder
import coil3.gif.GifDecoder
import coil3.request.crossfade
import com.aescalantedev.tenyo.core.data.di.dataModule
import com.aescalantedev.tenyo.core.domain.di.domainModule
import com.aescalantedev.tenyo.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

/**
 * Clase [Application] principal de Tenyo.
 *
 * Responsabilidades en el inicio del proceso:
 * 1. Inicializa el grafo de inyeccion de dependencias con Koin unificando los modulos:
 *    - [domainModule] (`:core:domain`)
 *    - [dataModule] (`:core:data`)
 *    - [appModule] (`:app`)
 * 2. Configura [SingletonImageLoader.Factory] de Coil 3 habilitando el decodificador nativo de GIFs
 *    ([AnimatedImageDecoder] en API 28+ y [GifDecoder] como respaldo) para reproducir las animaciones
 *    de ejecucion del dataset de ejercicios.
 * 3. Inicializa el runtime nativo C++ de Rive ([Rive.init]) para el selector anatomico interactivo.
 */
class TenyoApplication : Application(), SingletonImageLoader.Factory {

    override fun onCreate() {
        super.onCreate()
        Rive.init(this)
        startKoin {
            androidContext(this@TenyoApplication)
            modules(
                domainModule,
                dataModule,
                appModule
            )
        }
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return ImageLoader.Builder(context)
            .components {
                if (Build.VERSION.SDK_INT >= 28) {
                    add(AnimatedImageDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .crossfade(true)
            .build()
    }
}
