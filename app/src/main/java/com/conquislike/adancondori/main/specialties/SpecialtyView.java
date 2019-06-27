package com.conquislike.adancondori.main.specialties;

import com.conquislike.adancondori.main.base.BaseView;

public interface SpecialtyView extends BaseView {
    void setName(String username);

    String getNameText();

    void setNameError(String string);
}
