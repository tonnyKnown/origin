package com.example.aiinterview.service.ai;

import java.util.Map;

/**
 * 面试方向画像：按赛道（职业方向一级分类）给出出题关注点和题型映射，
 * 替代原先硬编码在 prompt 里的“Java 简历画像”，保证任何方向都能得到贴合方向的题。
 */
public final class DirectionProfile {

    /** 5 种题型的顺序固定，具体含义按赛道解释。 */
    public static final String[] QUESTION_TYPES = {"BASIC", "CONCURRENCY", "MIDDLEWARE", "PROJECT", "ARCHITECTURE"};

    private final String skillScope;
    private final Map<String, String> categoryFocus;

    private DirectionProfile(String skillScope, Map<String, String> categoryFocus) {
        this.skillScope = skillScope;
        this.categoryFocus = categoryFocus;
    }

    public String skillScope() {
        return skillScope;
    }

    public String categoryFocus(String questionType) {
        return categoryFocus.getOrDefault(questionType,
                "围绕该方向的高频核心知识点出题，题目需与方向技术栈强相关。");
    }

    /**
     * 根据方向路径（如“软件开发方向 / Java / Java 后端开发”）解析赛道画像。
     * 未匹配到已知赛道时退化为通用画像，仍然以方向路径本身为中心出题。
     */
    public static DirectionProfile resolve(String positionType) {
        String track = firstSegment(positionType);
        if (track.contains("运维") || track.contains("DevOps") || track.contains("云原生")) {
            return new DirectionProfile(
                    "Linux 系统与 Shell、网络协议（TCP/HTTP/DNS）、Nginx 等 Web 服务、数据库与缓存运维、"
                            + "容器与 Kubernetes、CI/CD 流水线、监控告警体系、生产故障排查",
                    Map.of(
                            "BASIC", "基础题：Linux 进程/文件系统/权限、网络协议、常用命令与文本处理",
                            "CONCURRENCY", "性能与并发题：连接数与负载模型、进程线程调度、IO 模型、系统瓶颈定位",
                            "MIDDLEWARE", "中间件与平台题：Nginx/数据库/缓存/消息队列/容器平台的部署、调优与排障",
                            "PROJECT", "项目实战题：生产故障定位、容量规划、变更与回滚、脚本与自动化落地",
                            "ARCHITECTURE", "架构设计题：高可用部署、灾备方案、自动化交付体系、云原生整体架构"
                    ));
        }
        if (track.contains("硬件")) {
            return new DirectionProfile(
                    "电路设计、PCB 布局布线、电源与信号完整性、总线与接口协议、嵌入式硬件、"
                            + "测试测量仪器、EMC/EMI、元器件选型",
                    Map.of(
                            "BASIC", "基础题：模拟/数字电路、常用元器件特性、单位与计算",
                            "CONCURRENCY", "信号与时序题：信号完整性、时序收敛、高速接口与干扰处理",
                            "MIDDLEWARE", "平台与工具题：测试仪器使用、仿真工具、EDA 工具链、总线协议分析",
                            "PROJECT", "项目实战题：原理图到 PCB 的完整落地、调试与问题定位、量产与成本权衡",
                            "ARCHITECTURE", "架构设计题：硬件系统架构划分、电源树与散热设计、可测试性与可靠性设计"
                    ));
        }
        if (track.contains("机器人")) {
            return new DirectionProfile(
                    "运动控制、SLAM 与建图、路径规划与导航、ROS/中间件、传感器融合、"
                            + "实时系统、仿真环境、机电一体化",
                    Map.of(
                            "BASIC", "基础题：坐标系变换、运动学基础、常用传感器原理、控制理论基础",
                            "CONCURRENCY", "实时与并发题：实时系统调度、控制周期与延迟、多传感器时序同步",
                            "MIDDLEWARE", "平台与工具题：ROS 通信机制、仿真工具链、驱动与标定流程",
                            "PROJECT", "项目实战题：机器人项目落地、调试与问题定位、实机到仿真验证",
                            "ARCHITECTURE", "架构设计题：机器人软件架构分层、导航与控制模块划分、故障恢复策略"
                    ));
        }
        if (track.contains("软件") || track.contains("开发") || track.contains("AI")) {
            return new DirectionProfile(
                    "以方向路径中声明的语言和技术栈为中心：语言基础、数据结构与算法、框架与工程化、"
                            + "数据库、缓存与消息、运行时与性能排查；方向涉及 AI 时包含 Agent、Embedding、"
                            + "Prompt 工程、Function Calling 等应用能力",
                    Map.of(
                            "BASIC", "基础题：该方向主力语言的集合/泛型/反射/IO 等核心语法与常用数据结构、算法",
                            "CONCURRENCY", "运行时与并发题：线程与内存模型、锁与并发工具、GC/运行时调优与排查",
                            "MIDDLEWARE", "中间件题：数据库、缓存、消息队列、搜索等组件的原理、优化与一致性",
                            "PROJECT", "项目实战题：框架落地、事务、慢查询与性能问题的完整排查过程",
                            "ARCHITECTURE", "架构设计题：微服务高可用、分布式事务、高并发设计；涉及 AI 方向时含 LLM 应用架构"
                    ));
        }
        return new DirectionProfile(
                "围绕“" + positionType + "”方向的高频核心知识、工程实践与系统设计能力",
                Map.of(
                        "BASIC", "基础题：该方向最核心的概念与原理",
                        "CONCURRENCY", "进阶题：性能、并发或时序相关的难点",
                        "MIDDLEWARE", "工具链题：该方向常用工具、平台或中间件的原理与使用",
                        "PROJECT", "项目实战题：真实落地经验与问题排查",
                        "ARCHITECTURE", "架构设计题：整体方案设计与权衡"
                ));
    }

    private static String firstSegment(String positionType) {
        if (positionType == null || positionType.isBlank()) {
            return "";
        }
        int slash = positionType.indexOf('/');
        return slash > 0 ? positionType.substring(0, slash).trim() : positionType.trim();
    }
}
