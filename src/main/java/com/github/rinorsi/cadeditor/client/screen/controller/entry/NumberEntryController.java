package com.github.rinorsi.cadeditor.client.screen.controller.entry;

import com.github.rinorsi.cadeditor.client.screen.model.entry.NumberEntryModel;
import com.github.rinorsi.cadeditor.client.screen.view.entry.NumberEntryView;
import java.lang.Number;
import java.util.Objects;


public class NumberEntryController<N extends Number> extends ValueEntryController<NumberEntryModel<N>, NumberEntryView> {
    public NumberEntryController(NumberEntryModel<N> model, NumberEntryView view) {
        super(model, view);
    }

    
    @Override 
    public void bind() {
        super.bind();
        ((NumberEntryView) this.view).getTextField().setValidator(((NumberEntryModel) getModel()).getTextPredicate().and(s -> ((NumberEntryModel) getModel()).validate((Number) ((NumberEntryModel) getModel()).getToNumberFunction().apply(s))));
        ((NumberEntryView) this.view).getTextField().textProperty().addListener(value -> {
            if (((NumberEntryView) this.view).getTextField().isValid()) {
                ((NumberEntryModel) this.model).setValue((Number) ((NumberEntryModel) getModel()).getToNumberFunction().apply(value));
                ((NumberEntryModel) this.model).setValid(true);
            }
        });
        ((NumberEntryModel) this.model).valueProperty().addListener(newValue -> {
            ((NumberEntryView) this.view).getTextField().setText((String) ((NumberEntryModel) this.model).getToStringFunction().apply(newValue));
        });
        ((NumberEntryView) this.view).getTextField().setText((String) ((NumberEntryModel) this.model).getToStringFunction().apply((Number) ((NumberEntryModel) this.model).getValue()));
        ((NumberEntryView) this.view).getTextField().validProperty().addListener(((NumberEntryModel) this.model)::setValid);
        ((NumberEntryView) this.view).getTextField().onKeyPress(event -> {
            if (event.isConsumed()) {
                return;
            }
            int keyCode = event.getKeyCode();
            if (keyCode != 265 && keyCode != 264) {
                return;
            }
            String currentText = ((NumberEntryView) this.view).getTextField().getText();
            if (!((NumberEntryModel) this.model).getTextPredicate().test(currentText)) {
                return;
            }
            try {
                Number number = (Number) ((NumberEntryModel) this.model).getToNumberFunction().apply(currentText);
                double step = ((NumberEntryModel) this.model).getArrowStep();
                if (event.isShiftKeyDown()) {
                    step = ((NumberEntryModel) this.model).getArrowStepShift();
                } else if (event.isControlKeyDown()) {
                    step = ((NumberEntryModel) this.model).getArrowStepCtrl();
                }
                if (keyCode == 264) {
                    step = -step;
                }
                Number numberOffsetValue = ((NumberEntryModel) this.model).offsetValue(number, step);
                if (!((NumberEntryModel) this.model).validate(numberOffsetValue) || Objects.equals(numberOffsetValue, number)) {
                    return;
                }
                ((NumberEntryModel) this.model).setValue(numberOffsetValue);
                event.consume();
            } catch (Exception e) {
            }
        });
    }
}
