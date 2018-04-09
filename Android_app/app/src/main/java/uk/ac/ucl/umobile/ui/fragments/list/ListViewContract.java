package uk.ac.ucl.umobile.ui.fragments.list;

import uk.ac.ucl.umobile.ui.fragments.ViewContract;

public interface ListViewContract<I, N> extends ViewContract<I> {
    void showListFooter(boolean show);

    void handleNextItems(N result);
}
