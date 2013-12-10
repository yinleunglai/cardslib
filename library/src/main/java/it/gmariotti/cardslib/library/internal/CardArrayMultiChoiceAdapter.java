/*
 * ******************************************************************************
 *   Copyright (c) 2013 Gabriele Mariotti.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *  *****************************************************************************
 */

package it.gmariotti.cardslib.library.internal;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;

import java.util.List;

import it.gmariotti.cardslib.library.R;
import it.gmariotti.cardslib.library.internal.multichoice.MultiChoiceAdapter;
import it.gmariotti.cardslib.library.internal.multichoice.MultiChoiceAdapterHelperBase;
import it.gmariotti.cardslib.library.view.CardListView;
import it.gmariotti.cardslib.library.view.CardView;

/**
 * @author Gabriele Mariotti (gabri.mariotti@gmail.com)
 */
public abstract class CardArrayMultiChoiceAdapter extends CardArrayAdapter implements MultiChoiceAdapter,AbsListView.MultiChoiceModeListener{

    private MultiChoiceAdapterHelperBase mHelper = new MultiChoiceAdapterHelperBase(this);

    protected ActionMode actionMode;
    protected String mTitleSelected;
    private static final String BUNDLE_KEY = "card__selection";

    // -------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------

    /**
     * Constructor
     *
     * @param context The current context.
     * @param cards   The cards to represent in the ListView.
     */
    public CardArrayMultiChoiceAdapter(Context context, List<Card> cards) {
        super(context, cards);
        mHelper.setMultiChoiceModeListener(this);
    }

    public void restoreSelectionFromSavedInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }
        mTitleSelected=savedInstanceState.getString(BUNDLE_KEY);
    }
    // -------------------------------------------------------------
    // Adapter
    // -------------------------------------------------------------

    @Override
    public void setCardListView(CardListView cardListView) {
        super.setCardListView(cardListView);
        cardListView.setItemsCanFocus(false);
        mHelper.setAdapterView(cardListView);
    }

    @Override
    protected void setupMultichoice(Card mCard, CardView mCardView,long position) {
        super.setupMultichoice(mCard,mCardView,position);

        if (mCard.getOnClickListener()!=null){
            mCardView.setOnClickListener(null);
            mCardListView.setClickable(true);
            mCardListView.setOnItemClickListener(mHelper);
        }

        mCardView.setLongClickable(true);

    }

    // -------------------------------------------------------------
    // ActionMode
    // -------------------------------------------------------------

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        actionMode=mode;
        return false;
      }


    @Override
    public void onDestroyActionMode(ActionMode mode) {
       actionMode=null;
    }


    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        onItemSelectedStateChanged(mode);
        Card card = getItem(position);
        onItemCheckedStateChanged(mode, position, id, checked, card.getCardView(), card);
    }


    protected void onItemSelectedStateChanged(ActionMode mode) {
        int count =  mCardListView.getCheckedItemCount();
       
        Resources res = mCardListView.getResources();
        mTitleSelected = res.getQuantityString(R.plurals.card_selected_items, count, count);
        mode.setTitle(mTitleSelected);
    }

    protected abstract void onItemCheckedStateChanged (ActionMode mode, int position, long id, boolean checked,CardView cardView,Card card);

    // -------------------------------------------------------------
    // MultiChoice
    // -------------------------------------------------------------

    public void save(Bundle outState) {
        outState.putString(BUNDLE_KEY, mTitleSelected);
    }


    @Override
    public boolean isCardCheckable(int position) {
        Card card = getItem(position);
        if (card!=null)
            return card.isCheckable();

        return false;
    }

    @Override
    public boolean isActionModeStarted() {
        if (actionMode!=null){
            return true;
        }
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Card mCard= getItem(position);
        if (mCard!=null && mCard.getOnClickListener()!=null)
            mCard.getOnClickListener().onClick(mCard,view);
    }

}
