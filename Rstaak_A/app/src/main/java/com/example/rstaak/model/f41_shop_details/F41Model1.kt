package com.example.rstaak.model.f41_shop_details

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

data class F41Model1 @AssistedInject constructor(
    @Assisted var childs: List<F41Child>,
    @Assisted("parentCategoryId") var parentCategoryId:String,
    @Assisted("parentCategoryName") var parentCategoryName: String,
    @Assisted("shopId") var shopId: String): F41ParentModel(Main)
{
        @AssistedFactory
        interface Factory
        {
            fun create(
                childs: List<F41Child>,
                @Assisted("parentCategoryId") parentCategoryId:String,
                @Assisted("parentCategoryName") parentCategoryName: String,
                @Assisted("shopId")  shopId: String): F41Model1

        }
    }
