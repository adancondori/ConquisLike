package com.conquislike.adancondori.main.search.users;

import com.conquislike.adancondori.main.base.BaseFragmentView;
import com.conquislike.adancondori.model.Profile;

import java.util.List;

/**
 * Created by Alexey on 08.06.18.
 */
public interface SearchUsersView extends BaseFragmentView {
    void onSearchResultsReady(List<Profile> profiles);

    void showLocalProgress();

    void hideLocalProgress();

    void showEmptyListLayout();

    void updateSelectedItem();
}
