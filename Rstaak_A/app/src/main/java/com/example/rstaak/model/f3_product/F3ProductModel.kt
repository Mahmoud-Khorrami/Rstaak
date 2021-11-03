package com.example.rstaak.model.f3_product

import android.os.Parcelable
import com.example.rstaak.req_res.f3_product.RstaakLocation
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.android.parcel.Parcelize

@Parcelize
class F3ProductModel @AssistedInject constructor(
                    @Assisted("categoryId") var categoryId: String,
                    @Assisted("categoryName") var categoryName: String,
                    @Assisted("createdDatetime") var createdDatetime: String,
                    @Assisted("description") var description: String,
                    @Assisted("ifPublished") var ifPublished: Boolean,
                    @Assisted("ifUsed") var ifUsed: Boolean,
                    @Assisted("imageList") var imageList: List<String>,
                    @Assisted("likeFlag") var likeFlag: Boolean,
                    @Assisted("likedNumber") var likedNumber: Int,
                    @Assisted("modified_on") var modified_on: String? = null,
                    @Assisted("owner") var owner: String,
                    @Assisted("ownerCellNumber") var ownerCellNumber: String,
                    @Assisted("price") var price: Int,
                    @Assisted("productId") var productId: String,
                    @Assisted("rostaakLocation") var rostaakLocation: RstaakLocation,
                    @Assisted("shopId") var shopId: String,
                    @Assisted("shopName") var shopName: String,
                    @Assisted("title") var title: String,
                    @Assisted("viewedNumber") var viewedNumber: Int,
                    @Assisted("vitrin") var vitrin: Boolean): F3ProductParentModel(Main), Parcelable
{
    @AssistedFactory
    interface Factory
    {
        fun create(
            @Assisted("categoryId") categoryId: String,
            @Assisted("categoryName") categoryName: String,
            @Assisted("createdDatetime") createdDatetime: String,
            @Assisted("description") description: String,
            @Assisted("ifPublished") ifPublished: Boolean,
            @Assisted("ifUsed") ifUsed: Boolean,
            @Assisted("imageList") imageList: List<String>,
            @Assisted("likeFlag") likeFlag: Boolean,
            @Assisted("likedNumber") likedNumber: Int,
            @Assisted("modified_on") modified_on: String? = null,
            @Assisted("owner") owner: String,
            @Assisted("ownerCellNumber") ownerCellNumber: String,
            @Assisted("price") price: Int,
            @Assisted("productId") productId: String,
            @Assisted("rostaakLocation") rostaakLocation: RstaakLocation,
            @Assisted("shopId") shopId: String,
            @Assisted("shopName") shopName: String,
            @Assisted("title") title: String,
            @Assisted("viewedNumber") viewedNumber: Int,
            @Assisted("vitrin") vitrin: Boolean): F3ProductModel
    }
}
