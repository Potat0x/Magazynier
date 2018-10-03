package magazynier;

import javafx.embed.swing.JFXPanel;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import magazynier.utils.MaxTextFieldLengthFormValidator;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class MaxTextFieldLengthFormValidatorTest {

    @BeforeClass
    public static void initJavaFX() {
        new JFXPanel();
    }

    @Test
    public void testWithoutCustomLimits() {
        final int MAX_LENGTH = 2;
        TextField textField = new TextField();
        GridPane gridPane = new GridPane();
        gridPane.add(textField, 0, 0);
        gridPane.add(new TextField("x"), 1, 0);

        assertTrue(MaxTextFieldLengthFormValidator.test(new GridPane(), MAX_LENGTH));

        textField.setText(null);
        assertTrue(MaxTextFieldLengthFormValidator.test(gridPane, MAX_LENGTH));
        textField.setText("");
        assertTrue(MaxTextFieldLengthFormValidator.test(gridPane, MAX_LENGTH));
        textField.setText("1");
        assertTrue(MaxTextFieldLengthFormValidator.test(gridPane, MAX_LENGTH));
        textField.setText("22");
        assertTrue(MaxTextFieldLengthFormValidator.test(gridPane, MAX_LENGTH));
        textField.setText("333");
        assertFalse(MaxTextFieldLengthFormValidator.test(gridPane, MAX_LENGTH));
    }

    @Test
    public void testWithCustomLimits() {
        final int MAX_LENGTH = 2;
        final int MAX_CUSTOM_LENGTH = 5;

        TextField textField1 = new TextField("1");
        TextField textField2 = new TextField("22");
        TextField textFieldWithCustomLength = new TextField("333");

        VBox vbox = new VBox();
        vbox.getChildren().addAll(textField1, textField2, textFieldWithCustomLength);

        assertFalse(MaxTextFieldLengthFormValidator.test(vbox, MAX_LENGTH));
        assertFalse(MaxTextFieldLengthFormValidator.test(vbox, MAX_LENGTH, new HashMap<>()));
        assertTrue(MaxTextFieldLengthFormValidator.test(new VBox(), MAX_LENGTH, new HashMap<>()));

        Map<TextField, Integer> customLimits = new HashMap<>();
        customLimits.put(textFieldWithCustomLength, MAX_CUSTOM_LENGTH);
        assertTrue(MaxTextFieldLengthFormValidator.test(vbox, MAX_LENGTH, customLimits));
    }
}