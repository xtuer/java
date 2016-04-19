package com.xtuer.editor;

import com.xtuer.bean.User;
import com.xtuer.util.JsonUtil;

import java.beans.PropertyEditorSupport;

public class UserEditor extends PropertyEditorSupport {
    // "Alice, 23"
    @Override
    public void setAsText(String text) {
        String[] components = text.split(",");
        User user = new User();
        user.setUsername(components[0].trim());
        user.setAge(Integer.parseInt(components[1].trim()));
        super.setValue(user);
    }

    @Override
    public String getAsText() {
        return JsonUtil.toJson(super.getValue());
    }

    public static void main(String[] args) {
        UserEditor editor = new UserEditor();
        editor.setAsText("Alice, 23");
        System.out.println(editor.getAsText());
    }
}
