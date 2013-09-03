package org.raml.parser.rule;

import java.util.ArrayList;
import java.util.List;

import org.yaml.snakeyaml.nodes.ScalarNode;


public class EnumModifierRule extends SimpleRule
{

    private SimpleRule enumRule;
    private List<String> enumTypes;

    public EnumModifierRule(String ruleName, List<String> enumTypes, SimpleRule enumRule)
    {
        super(ruleName, Integer.class);
        this.enumTypes = enumTypes;
        this.enumRule = enumRule;
    }

    @Override
    public List<ValidationResult> validateKey(ScalarNode key)
    {
        List<ValidationResult> validationResults = new ArrayList<ValidationResult>();
        ScalarNode enumValueNode = enumRule.getValueNode();
        String messageTypes = generateMessageTypes(enumTypes, enumRule);
        if (enumValueNode == null)
        {
            validationResults.add(ValidationResult.createErrorResult(enumRule.getName() + " must exist first, and it must be of type" + messageTypes, key.getStartMark(), key.getEndMark()));
        }
        if (enumValueNode != null && !enumTypes.contains(enumRule.getValueNode().getValue()))
        {
            validationResults.add(ValidationResult.createErrorResult(enumRule.getName() + " must be of type" + messageTypes, key.getStartMark(), key.getEndMark()));
        }
        validationResults.addAll(super.validateKey(key));
        if (ValidationResult.areValid(validationResults))
        {
            setKeyNode(key);
        }
        return validationResults;
    }

    private String generateMessageTypes(List<String> enumTypes2, SimpleRule enumRule2)
    {
        StringBuilder types = new StringBuilder();
        for (int i = 0; i < enumTypes.size() - 1; i++)
        {
            types.append(" " + enumTypes.get(i) + " or");
        }
        types.append(" " + enumTypes.get(enumTypes.size() - 1));
        return types.toString();
    }

    @Override
    public List<ValidationResult> validateValue(ScalarNode node)
    {
        String valueNode = node.getValue();
        List<ValidationResult> validationResults = new ArrayList<ValidationResult>();
        try
        {
            Integer.parseInt(valueNode);
        }
        catch (NumberFormatException nfe)
        {
            validationResults.add(ValidationResult.createErrorResult(getName() + " can only contain integer values greater than zero", node.getStartMark(), node.getEndMark()));
        }
        validationResults.addAll(super.validateValue(node));
        return validationResults;
    }
}
