package frontend.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.google.common.eventbus.EventBus;

import frontend.event.EnabledChangedEvent;

public class InputText {
	private final StyledText text;
	private final EditFunctions editFunctions;
	
	public InputText(final EventBus eventBus, Composite parent) {
		text = new StyledText(parent, SWT.WRAP | SWT.V_SCROLL);
		text.setMargins(2, 2, 2, 2);
		
		editFunctions = new EditFunctions(text);
		
		text.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent event) {
				if((event.stateMask & SWT.CTRL) > 0) {
					if(event.keyCode == 'a') {
						editFunctions.selectAll();
					}
				}
			}
		});

		text.setFont(FontList.consolas8);
		
		text.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent event) {
				eventBus.post(new EnabledChangedEvent());
			}

			public void focusLost(FocusEvent event) {
				eventBus.post(new EnabledChangedEvent());
			}
		});
	}
	
	public String getText() {
		return editFunctions.getText();
	}
	
	public void setText(String text) {
		editFunctions.setText(text);
	}
	
	public Control getControl() {
		return text;
	}

	public boolean hasFocus() {
		return text.isFocusControl();
	}

	public EditFunctions getEditFunctions() {
		return editFunctions;
	}
}
