package jp.techacademy.sato.hiroka.apiapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_web_view.*

class WebViewActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val strShop = intent.getStringExtra(KEY_SHOP)
        val shop = Gson().fromJson<Shop>(strShop, Shop::class.java)

        // お気に入り状態を取得
        var isFavorite = FavoriteShop.findBy(shop.id) != null

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        webView.loadUrl(if (shop.couponUrls.sp.isNotEmpty()) shop.couponUrls.sp else shop.couponUrls.pc)

        if (isFavorite) {
            favorite.text = "お気に入り削除"
        } else {
            favorite.text = "お気に入り登録"
        }

        favorite.setOnClickListener {
            if (isFavorite) {
                showConfirmDeleteFavoriteDialog(shop.id)
            } else {
                FavoriteShop.insert(FavoriteShop().apply {
                    id = shop.id
                    name = shop.name
                    address = shop.address
                    imageUrl = shop.logoImage
                    url = if (shop.couponUrls.sp.isNotEmpty()) shop.couponUrls.sp else shop.couponUrls.pc
                })
                favorite.text = "お気に入り削除"
            }

            isFavorite = FavoriteShop.findBy(shop.id) != null
        }
    }

    companion object {
        private const val KEY_SHOP = "key_shop"
        fun start(activity: Activity, shop: Shop?) {
            activity.startActivity(Intent(activity, WebViewActivity::class.java).putExtra(KEY_SHOP, Gson().toJson(shop)))
        }
    }

    private fun showConfirmDeleteFavoriteDialog(id: String) {
        AlertDialog.Builder(this)
            .setTitle(R.string.delete_favorite_dialog_title)
            .setMessage(R.string.delete_favorite_dialog_message)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                FavoriteShop.delete(id)
                favorite.text = "お気に入り登録"
            }
            .setNegativeButton(android.R.string.cancel) { _, _ ->}
            .create()
            .show()
    }
}