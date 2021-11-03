package com.example.rstaak.model.f323_category_select

import android.os.Parcelable
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.android.parcel.Parcelize

@Parcelize
data class F323Model @AssistedInject constructor(
    @Assisted("categoryId") var categoryId: String,
    @Assisted("title") var title: String,
    @Assisted("parentName") var parentName: String? = null,
    @Assisted var imageList: List<String>? = null,
    @Assisted var childCategories: List<F323ChildCategoryModel>? = null,
    @Assisted var child: Boolean): F323ParentModel(Main), Parcelable
{
    @AssistedFactory
    interface Factory
    {
        fun create(
            @Assisted("categoryId") categoryId: String,
            @Assisted("title") title: String,
            @Assisted("parentName") parentName: String? = null,
            imageList: List<String>? = null,
            childCategories: List<F323ChildCategoryModel>? = null,
            child: Boolean): F323Model
    }
}
