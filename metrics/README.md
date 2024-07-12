## 压力测试

```bash
jmeter -n -t test.jmx -l result.jtl -e -o ./report
```

* -n 不使用界面启动。
* -t 指定测试计划文件。
* -l 中间结果文件，保存日志。
* -o 输出目录，生成HTML报告。