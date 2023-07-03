package ntou.soselab.chatops4msa.Service.ToolkitFunctionService;

import ntou.soselab.chatops4msa.Exception.ToolkitFunctionException;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class MathToolkit extends ToolkitFunction {

    /**
     * @param expression like 2 * 3 / 4
     * @return like 1.5
     */
    public String toolkitMathCalculate(String expression) throws ToolkitFunctionException {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("js");
        try {
            return engine.eval(expression).toString();
        } catch (ScriptException e) {
            e.printStackTrace();
            throw new ToolkitFunctionException("toolkit-math-calculate error");
        }
    }
}
