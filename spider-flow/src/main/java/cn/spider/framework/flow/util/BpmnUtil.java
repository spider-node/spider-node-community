package cn.spider.framework.flow.util;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperties;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperty;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * BPMN工具类，用于处理BPMN规范字符串
 *
 * @author dds
 * @since 2025/9/17
 */
public class BpmnUtil {

    /**
     * 为指定ID的task节点添加Camunda扩展属性，如果属性已存在则覆盖
     *
     * @param bpmnXml BPMN规范的XML字符串
     * @param taskId task节点ID
     * @param propertyName 属性名称
     * @param propertyValue 属性值
     * @return 添加属性后的BPMN XML字符串
     */
    public static String addCamundaProperty(String bpmnXml, String taskId, String propertyName, String propertyValue) {
        // 将XML字符串转换为BpmnModelInstance
        BpmnModelInstance modelInstance = Bpmn.readModelFromStream(new ByteArrayInputStream(bpmnXml.getBytes(StandardCharsets.UTF_8)));

        // 查找指定ID的task节点
        FlowElement flowElement = modelInstance.getModelElementById(taskId);
        if (flowElement == null) {
            throw new IllegalArgumentException("Task with id '" + taskId + "' not found");
        }

        if (!(flowElement instanceof Task)) {
            throw new IllegalArgumentException("Element with id '" + taskId + "' is not a task");
        }

        Task task = (Task) flowElement;

        // 获取或创建扩展元素
        ExtensionElements extensionElements = task.getExtensionElements();
        if (extensionElements == null) {
            extensionElements = modelInstance.newInstance(ExtensionElements.class);
            task.setExtensionElements(extensionElements);
        }

        // 查找或创建Camunda属性集合
        CamundaProperties camundaProperties = extensionElements.getElementsQuery()
                .filterByType(CamundaProperties.class)
                .singleResult();

        if (camundaProperties == null) {
            camundaProperties = modelInstance.newInstance(CamundaProperties.class);
            extensionElements.addChildElement(camundaProperties);
        }

        // 查找是否已存在同名属性
        CamundaProperty existingProperty = null;
        for (CamundaProperty prop : camundaProperties.getCamundaProperties()) {
            if (propertyName.equals(prop.getCamundaName())) {
                existingProperty = prop;
                break;
            }
        }

        // 如果属性已存在，则更新值；否则创建新属性
        if (existingProperty != null) {
            existingProperty.setCamundaValue(propertyValue);
        } else {
            CamundaProperty property = modelInstance.newInstance(CamundaProperty.class);
            property.setCamundaName(propertyName);
            property.setCamundaValue(propertyValue);
            camundaProperties.getCamundaProperties().add(property);
        }

        return Bpmn.convertToString(modelInstance);
    }


    /**
     * 获取指定节点的所有Camunda扩展属性
     *
     * @param bpmnXml BPMN规范的XML字符串
     * @param taskId task节点ID
     * @return 所有扩展属性的键值对映射
     */
    public static Map<String, String> getCamundaProperties(String bpmnXml, String taskId) {
        BpmnModelInstance modelInstance = Bpmn.readModelFromStream(new ByteArrayInputStream(bpmnXml.getBytes(StandardCharsets.UTF_8)));
        FlowElement flowElement = modelInstance.getModelElementById(taskId);

        if (flowElement == null) {
            throw new IllegalArgumentException("Task with id '" + taskId + "' not found");
        }

        if (!(flowElement instanceof Task)) {
            throw new IllegalArgumentException("Element with id '" + taskId + "' is not a task");
        }

        Task task = (Task) flowElement;
        ExtensionElements extensionElements = task.getExtensionElements();

        if (extensionElements == null) {
            return new HashMap<>(); // 返回空映射
        }

        CamundaProperties camundaProperties = extensionElements.getElementsQuery()
                .filterByType(CamundaProperties.class)
                .singleResult();

        if (camundaProperties == null) {
            return new HashMap<>(); // 返回空映射
        }

        // 收集所有属性
        return camundaProperties.getCamundaProperties().stream()
                .collect(java.util.stream.Collectors.toMap(
                        CamundaProperty::getCamundaName,
                        CamundaProperty::getCamundaValue
                ));
    }

    /**
     * 删除指定节点的特定Camunda属性
     *
     * @param bpmnXml BPMN规范的XML字符串
     * @param taskId task节点ID
     * @param propertyName 属性名称
     * @return 删除属性后的BPMN XML字符串
     */
    public static String removeCamundaProperty(String bpmnXml, String taskId, String propertyName) {
        BpmnModelInstance modelInstance = Bpmn.readModelFromStream(new ByteArrayInputStream(bpmnXml.getBytes(StandardCharsets.UTF_8)));
        FlowElement flowElement = modelInstance.getModelElementById(taskId);

        if (flowElement == null) {
            throw new IllegalArgumentException("Task with id '" + taskId + "' not found");
        }

        if (!(flowElement instanceof Task)) {
            throw new IllegalArgumentException("Element with id '" + taskId + "' is not a task");
        }

        Task task = (Task) flowElement;
        ExtensionElements extensionElements = task.getExtensionElements();

        if (extensionElements == null) {
            return bpmnXml; // 没有扩展元素，直接返回原XML
        }

        CamundaProperties camundaProperties = extensionElements.getElementsQuery()
                .filterByType(CamundaProperties.class)
                .singleResult();

        if (camundaProperties == null) {
            return bpmnXml; // 没有Camunda属性，直接返回原XML
        }

        // 查找并删除指定属性
        camundaProperties.getCamundaProperties().removeIf(property ->
                propertyName.equals(property.getCamundaName())
        );

        return Bpmn.convertToString(modelInstance);
    }

    /**
     * 获取BPMN模型中的所有流程ID
     *
     * @param bpmnXml BPMN规范的XML字符串
     * @return 流程ID列表
     */
    public static java.util.List<String> getProcessIds(String bpmnXml) {
        BpmnModelInstance modelInstance = Bpmn.readModelFromStream(new ByteArrayInputStream(bpmnXml.getBytes(StandardCharsets.UTF_8)));
        Collection<Process> processes = modelInstance.getModelElementsByType(Process.class);

        return processes.stream()
                .map(Process::getId)
                .collect(Collectors.toList());
    }

    /**
     * 获取指定流程中的所有任务节点ID
     *
     * @param bpmnXml BPMN规范的XML字符串
     * @param processId 流程ID
     * @return 任务节点ID列表
     */
    public static java.util.List<String> getTaskIds(String bpmnXml, String processId) {
        BpmnModelInstance modelInstance = Bpmn.readModelFromStream(new ByteArrayInputStream(bpmnXml.getBytes(StandardCharsets.UTF_8)));
        Process process = modelInstance.getModelElementById(processId);

        if (process == null) {
            throw new IllegalArgumentException("Process with id '" + processId + "' not found");
        }

        return process.getChildElementsByType(FlowElement.class).stream()
                .filter(element -> element instanceof Task)
                .map(FlowElement::getId)
                .collect(Collectors.toList());
    }

    /**
     * 添加新的任务节点到流程中
     *
     * @param bpmnXml BPMN规范的XML字符串
     * @param processId 流程ID
     * @param taskId 新任务节点ID
     * @param taskName 新任务节点名称
     * @return 添加任务后的BPMN XML字符串
     */
    public static String addTask(String bpmnXml, String processId, String taskId, String taskName) {
        BpmnModelInstance modelInstance = Bpmn.readModelFromStream(new ByteArrayInputStream(bpmnXml.getBytes(StandardCharsets.UTF_8)));
        Process process = modelInstance.getModelElementById(processId);

        if (process == null) {
            throw new IllegalArgumentException("Process with id '" + processId + "' not found");
        }

        // 创建新任务
        ServiceTask serviceTask = modelInstance.newInstance(ServiceTask.class);
        serviceTask.setId(taskId);
        serviceTask.setName(taskName);

        // 添加到流程中
        process.addChildElement(serviceTask);

        return Bpmn.convertToString(modelInstance);
    }

    /**
     * 删除指定的任务节点
     *
     * @param bpmnXml BPMN规范的XML字符串
     * @param taskId 任务节点ID
     * @return 删除任务后的BPMN XML字符串
     */
    public static String removeTask(String bpmnXml, String taskId) {
        BpmnModelInstance modelInstance = Bpmn.readModelFromStream(new ByteArrayInputStream(bpmnXml.getBytes(StandardCharsets.UTF_8)));
        FlowElement flowElement = modelInstance.getModelElementById(taskId);

        if (flowElement == null) {
            throw new IllegalArgumentException("Task with id '" + taskId + "' not found");
        }

        if (!(flowElement instanceof Task)) {
            throw new IllegalArgumentException("Element with id '" + taskId + "' is not a task");
        }

        // 从父元素中移除任务
        flowElement.getParentElement().removeChildElement(flowElement);

        return Bpmn.convertToString(modelInstance);
    }

    /**
     * 验证BPMN模型是否有效
     *
     * @param bpmnXml BPMN规范的XML字符串
     * @return 验证结果，true表示有效，false表示无效
     */
    public static boolean validateBpmn(String bpmnXml) {
        try {
            BpmnModelInstance modelInstance = Bpmn.readModelFromStream(new ByteArrayInputStream(bpmnXml.getBytes(StandardCharsets.UTF_8)));
            // 基本验证：检查是否能成功解析
            return modelInstance != null;
        } catch (Exception e) {
            return false;
        }
    }
}

