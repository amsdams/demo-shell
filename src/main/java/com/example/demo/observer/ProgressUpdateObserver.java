package com.example.demo.observer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.example.demo.shell.ProgressBar;
import com.example.demo.shell.ShellHelper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProgressUpdateObserver  implements PropertyChangeListener{

    @Autowired
    private ProgressBar progressBar;

    @Autowired
    private ShellHelper shellHelper;

    @Override
	public void propertyChange(PropertyChangeEvent evt) {
		ProgressUpdateEvent oldValue = (ProgressUpdateEvent) evt.getOldValue();
		log.info("oldValue {}", oldValue);
		ProgressUpdateEvent newValue = (ProgressUpdateEvent) evt.getNewValue();
		log.info("newValue {}", newValue);
		 int currentRecord = newValue.getCurrentCount().intValue();
	        int totalRecords = newValue.getTotalCount().intValue();

	        if (currentRecord == 0) {
	            // just in case the previous progress bar was interrupted
	            progressBar.reset();
	        }

	        String message = null;
	        int percentage = currentRecord * 100 / totalRecords;
	        if (StringUtils.hasText(newValue.getMessage())) {
	            message = shellHelper.getWarningMessage(newValue.getMessage());
	            progressBar.display(percentage, message);
	        }

	        progressBar.display(percentage, message);
	        if (percentage == 100) {
	            progressBar.reset();
	        }
	}
    
    
    
}
