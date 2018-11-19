package com.nze.nzeframework.widget.takephoto.permission;


import com.nze.nzeframework.widget.takephoto.model.InvokeParam;

/**
 * 授权管理回调
 */
public interface InvokeListener {
    PermissionManager.TPermissionType invoke(InvokeParam invokeParam);
}
