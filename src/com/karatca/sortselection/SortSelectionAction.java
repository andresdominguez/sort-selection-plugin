package com.karatca.sortselection;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.impl.DocumentImpl;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.project.Project;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author andresdom@google.com (Andres Dominguez)
 */
public class SortSelectionAction extends AnAction {

  private Project project;
  private DocumentImpl document;
  private EditorImpl editor;

  @Override
  public void update(AnActionEvent e) {
    e.getPresentation().setEnabled(e.getData(PlatformDataKeys.EDITOR) != null);
  }

  public void actionPerformed(AnActionEvent actionEvent) {
    project = actionEvent.getData(PlatformDataKeys.PROJECT);
    editor = (EditorImpl) actionEvent.getData(PlatformDataKeys.EDITOR);
    if (editor == null) {
      return;
    }
    document = (DocumentImpl) editor.getDocument();

    int selectionStart = editor.getSelectionModel().getSelectionStart();
    int selectionEnd = editor.getSelectionModel().getSelectionEnd();

    String selectedText = editor.getSelectionModel().getSelectedText();

    String[] split = selectedText.split(",");
    List<String> strings = Arrays.asList(split);
    Collections.sort(strings, new Comparator<String>() {
      public int compare(String left, String right) {
        return left.trim().compareTo(right.trim());
      }
    });
    StringBuffer buffer = new StringBuffer();
    for (String s : strings) {
      buffer.append(s).append(",");
    }
    if (buffer.length() > 0) {
      buffer.deleteCharAt(buffer.length() - 1);
    }

    String s = buffer.toString();

    runWriteActionInsideCommand(selectionStart, selectionEnd, s);
  }

  private void runWriteActionInsideCommand(
      final int start, final int end, final String replacement) {
    CommandProcessor.getInstance().executeCommand(project, new Runnable() {
      @Override
      public void run() {
        ApplicationManager.getApplication().runWriteAction(new Runnable() {
          @Override
          public void run() {
            document.replaceString(start, end, replacement);
          }
        });
      }
    }, "Sort selection", null);
  }
}
