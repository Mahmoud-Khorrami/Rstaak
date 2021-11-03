package com.example.rstaak.model.f41_shop_details

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

data class F41Model2 @AssistedInject constructor(
    @Assisted("shopId") var shopId: String,
    @Assisted("image") var image: String,
    @Assisted("childCategoryId") var childCategoryId: String,
    @Assisted("childCategoryName") var childCategoryName: String,
    @Assisted var productNumbers: Int): F41ParentModel(Main)
{
        @AssistedFactory
        interface Factory
        {
            fun create(
                @Assisted("shopId") shopId: String,
                @Assisted("image") image: String,
                @Assisted("childCategoryId") childCategoryId: String,
                @Assisted("childCategoryName") childCategoryName: String,
                productNumbers: Int): F41Model2
        }
    }
