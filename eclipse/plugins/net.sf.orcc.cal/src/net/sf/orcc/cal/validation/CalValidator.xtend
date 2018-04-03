/*
 * generated by Xtext
 */
package net.sf.orcc.cal.validation

import com.google.inject.Inject
import net.sf.orcc.cal.cal.AstEntity
import org.eclipse.xtext.validation.CheckType
import org.eclipse.xtext.validation.Check
import java.util.List
import java.util.ArrayList
import net.sf.orcc.cal.services.TypeCycleDetector

//import org.eclipse.xtext.validation.Check

/**
 * This class describes the validation of an RVC-CAL actor. The checks tagged as
 * "normal" are only performed when the file is saved and before code
 * generation.
 * 
 * 
 * @author Matthieu Wipliez
 * @author Endri Bezati
 * 
 */
class CalValidator extends AbstractCalValidator {

	@Inject
	private StructuralValidator structuralValidator;

	@Inject
	private WarningValidator warningValidator;

	@Inject
	private TypeValidator typeValidator;


	@Check(CheckType.NORMAL)
	def checkAstEntity(AstEntity entity) {
		// to hold errors
		var List<ValidationError> errors = new ArrayList<ValidationError>(0);

		// check there are no cycles in type definitions
		if (new TypeCycleDetector().detectCycles(entity, errors)) {
			showErrors(errors);

			// don't perform further checks, because cyclic type definitions
			// mess things up
			return;
		}

		// perform structural validation and type checking
		structuralValidator.validate(entity, getChain(), getContext());
		typeValidator.validate(entity, getChain(), getContext());
		warningValidator.validate(entity, getChain(), getContext());
	}

	def showErrors(List<ValidationError> errors) {
		for (ValidationError error : errors) {
			error(error.getMessage(), error.getSource(), error.getFeature(),
					error.getIndex());
		}
	}

}