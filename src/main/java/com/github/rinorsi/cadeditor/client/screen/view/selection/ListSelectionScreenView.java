package com.github.rinorsi.cadeditor.client.screen.view.selection;

import com.github.franckyi.guapi.api.GuapiHelper;
import com.github.franckyi.guapi.api.node.Button;
import com.github.franckyi.guapi.api.node.EnumButton;
import com.github.franckyi.guapi.api.node.ListView;
import com.github.franckyi.guapi.api.node.Node;
import com.github.franckyi.guapi.api.node.TextField;
import com.github.franckyi.guapi.api.node.builder.HBoxBuilder;
import com.github.franckyi.guapi.api.node.builder.ListViewBuilder;
import com.github.franckyi.guapi.api.node.builder.VBoxBuilder;
import com.github.rinorsi.cadeditor.client.screen.model.selection.ListSelectionFilter;
import com.github.rinorsi.cadeditor.client.screen.model.selection.element.ListSelectionElementModel;
import com.github.rinorsi.cadeditor.client.screen.mvc.ListSelectionElementMVC;
import com.github.rinorsi.cadeditor.client.screen.view.ScreenView;
import com.github.rinorsi.cadeditor.common.ModTexts;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;


public class ListSelectionScreenView extends ScreenView {
    private ListView<ListSelectionElementModel> listView;
    private TextField searchField;
    private EnumButton<ListSelectionFilter> categoryFilterButton;
    private EnumButton<ListSelectionFilter> namespaceFilterButton;
    private Button loadAllButton;

    @Override 
    protected Node createEditor() {
        return GuapiHelper.hBox((Consumer<HBoxBuilder>) editor -> {
            editor.add(GuapiHelper.vBox(), 1);
            editor.add(GuapiHelper.vBox((Consumer<VBoxBuilder>) center -> {
                center.add(GuapiHelper.hBox((Consumer<HBoxBuilder>) filterRow -> {
                    this.categoryFilterButton = GuapiHelper.enumButton();
                    this.categoryFilterButton.setVisible(false);
                    this.categoryFilterButton.setMinWidth(0);
                    this.categoryFilterButton.setPrefWidth(0);
                    this.categoryFilterButton.setMaxWidth(0);
                    filterRow.add(this.categoryFilterButton);
                    this.namespaceFilterButton = GuapiHelper.enumButton();
                    this.namespaceFilterButton.setVisible(false);
                    this.namespaceFilterButton.setMinWidth(0);
                    this.namespaceFilterButton.setPrefWidth(0);
                    this.namespaceFilterButton.setMaxWidth(0);
                    filterRow.add(this.namespaceFilterButton);
                    filterRow.add(this.loadAllButton = GuapiHelper.button((Component) ModTexts.LOAD_ALL));
                    filterRow.spacing(5);
                }));
                center.add(GuapiHelper.hBox((Consumer<HBoxBuilder>) searchRow -> {
                    searchRow.add(this.searchField = GuapiHelper.textField().placeholder(ModTexts.SEARCH), 1);
                }));
                ListView<ListSelectionElementModel> listView = (ListView) ((ListViewBuilder) ((ListViewBuilder) GuapiHelper.listView(ListSelectionElementModel.class, 25).renderer(item -> GuapiHelper.mvc(ListSelectionElementMVC.INSTANCE, item))).padding(5)).childrenFocusable();
                this.listView = listView;
                center.add(listView, 1);
                ((VBoxBuilder) center.spacing(5)).fillWidth();
            }), 4);
            editor.add(GuapiHelper.vBox(), 1);
            ((HBoxBuilder) editor.spacing(10)).fillHeight();
        });
    }

    public ListView<ListSelectionElementModel> getListView() {
        return this.listView;
    }

    public TextField getSearchField() {
        return this.searchField;
    }

    public EnumButton<ListSelectionFilter> getCategoryFilterButton() {
        return this.categoryFilterButton;
    }

    public EnumButton<ListSelectionFilter> getNamespaceFilterButton() {
        return this.namespaceFilterButton;
    }

    public EnumButton<ListSelectionFilter> getFilterButton() {
        return this.categoryFilterButton;
    }

    public Button getLoadAllButton() {
        return this.loadAllButton;
    }
}
