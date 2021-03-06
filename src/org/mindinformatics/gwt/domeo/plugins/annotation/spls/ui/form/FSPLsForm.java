package org.mindinformatics.gwt.domeo.plugins.annotation.spls.ui.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mindinformatics.gwt.domeo.client.Domeo;
import org.mindinformatics.gwt.domeo.client.IDomeo;
import org.mindinformatics.gwt.domeo.client.ui.annotation.forms.AFormComponent;
import org.mindinformatics.gwt.domeo.client.ui.annotation.forms.AFormsManager;
import org.mindinformatics.gwt.domeo.client.ui.annotation.forms.text.TextAnnotationFormsPanel;
import org.mindinformatics.gwt.domeo.client.ui.content.AnnotationFrameWrapper;
import org.mindinformatics.gwt.domeo.model.AnnotationFactory;
import org.mindinformatics.gwt.domeo.model.MAnnotation;
import org.mindinformatics.gwt.domeo.model.persistence.AnnotationPersistenceManager;
import org.mindinformatics.gwt.domeo.model.selectors.MTextQuoteSelector;
import org.mindinformatics.gwt.domeo.plugins.annotation.spls.model.MPharmgx;
import org.mindinformatics.gwt.domeo.plugins.annotation.spls.model.MSPLPharmgxUsage;
import org.mindinformatics.gwt.domeo.plugins.annotation.spls.model.MSPLsAnnotation;
import org.mindinformatics.gwt.domeo.plugins.annotation.spls.model.SPLsFactory;
import org.mindinformatics.gwt.framework.component.resources.model.MLinkedResource;
import org.mindinformatics.gwt.framework.component.resources.model.ResourcesFactory;
import org.mindinformatics.gwt.framework.src.IResizable;
import org.mindinformatics.gwt.framework.widget.ButtonWithIcon;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TabBar;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Richard Boyce <rdb20@pitt.edu>
 */
public class FSPLsForm extends AFormComponent implements IResizable {

	private static Logger logger = Logger.getLogger("");

	public static final String LABEL = "SPL Annotation";
	public static final String LABEL_EDIT = "EDIT SPL ANNOTATION";

	public static final String LOG_CATEGORY_QUALIFIER_CREATE = "CREATING SPL ANNOTATION";
	public static final String LOG_CATEGORY_QUALIFIER_EDIT = "EDITING SPL ANNOTATION";

	public static final String SPL_POC_PREFIX = "http://purl.org/net/nlprepository/spl-pharmgx-annotation-poc#";

	interface Binder extends UiBinder<VerticalPanel, FSPLsForm> {
	}

	private static final Binder binder = GWT.create(Binder.class);

	private MSPLsAnnotation _item;
	private MPharmgx currentPharmgx;
	private ArrayList<Widget> tabs = new ArrayList<Widget>();

	@UiField
	VerticalPanel container;
	@UiField
	FlowPanel newQualifiers;
	@UiField
	HorizontalPanel buttonsPanel;
	@UiField
	ListBox annotationSet;
	@UiField
	VerticalPanel rightColumn;

	// Biomarkers
	@UiField
	ListBox descriptbm, descriptsvtlb, descriptstslb, descriptdoi;

	// PK Impact
	@UiField
	RadioButton descriptpkdm, descriptpkim, descriptpkia, descriptpkda,
			descriptpkid, descriptpkdd, descriptpkie, descriptpkde,
			descriptpkni, descriptpknone;

	// PD Impact
	@UiField
	RadioButton descriptpddt, descriptpdit, descriptpdir, descriptpdni,
			descriptpdie, descriptpdde, descriptpdnone;

	// recommendation drug
	@UiField
	CheckBox descriptdsca, descriptdsal, descriptdsam, descriptdsnr,
			descriptdsnc, descriptdsnone;

	// recommendation dose
	@UiField
	CheckBox descriptdrdfb, descriptdrifb, descriptdrnc, descriptdrus,
			descriptdrcs, descriptdrnone;

	// recommendation monitoring
	@UiField
	CheckBox descriptmreq, descriptmrec, descriptmnc, descriptmcms,
			descriptmcnone;

	// test section
	@UiField
	CheckBox descripttreq, descripttrec, descriptttna, descripttnn,
			descripttnone;

	// statements
	@UiField
	CheckBox descriptsai, descriptsmcc;

	/*
	 * @UiField ListBox descriptsmc;
	 */

	// population provalence
	@UiField
	CheckBox descriptppm;

	// alleles
	@UiField
	TextArea commentBody, allelesbody, medconditbody, descriptsvtbody,
			descriptstsbody;

	String[] biomarker = { "ApoE2", "BRAF", "C-Kit", "CCR5", "CD20_antigen",
			"CD25", "CD30", "CYP1A2", "CYP2C19", "CYP2C9", "CYP2D6", "DPD",
			"EGFR", "ER and PgR_receptor", "ER_receptor", "FIP1L1-PDGFRα",
			"G6PD", "HLA-B*1502", "HLA-B*5701", "Her2/neu", "IL28B", "KRAS",
			"LDL_Receptor", "NAT1;_NAT2", "PDGFR", "PML/RARα", "Rh_genotype",
			"TPMT", "UGT1A1", "VKORC1" };

	String[] varient = { "poor-metabolizer", "intermediate-metabolizer",
			"extensive-metabolizer", "ultra-metabolizer",
			"intermediate-activity", "low-or-absent-activity", "HLA-B*1502",
			"HLA-B*5701" };

	String[] test = { "gene-haplotype-positive", "gene-haplotype-negative",
			"gene-SNP-positive", "gene-SNP-negative", "fused-gene-positive",
			"fused-gene-negative", "gene-expression-level-high",
			"gene-expression-level-low", "biomarker-positive",
			"biomarker-negative", "biomarker-level-high",
			"biomarker-level-low", "poor-metabolizer-positive",
			"poor-metabolizer-negative", "ultra-metabolizer-positive",
			"ultra-metabolizer-negative", "chromosomal-aberration-positive",
			"chromosomal-aberration-negative" };

	String[] drugs = { "Abacivir", "Aripiprazole", "Arsenic_Trioxide",
			"Atomoxetine", "Atorvastatin", "Azathioprine", "Boceprevir",
			"Brentuximab_Vedotin", "Busulfan", "Capecitabine", "Carbamazepine",
			"Carisoprodol", "Carvedilol", "Celecoxib", "Cetuximab",
			"Cevimeline", "Chlordiazepoxide_and_Amitriptyline", "Chloroquine",
			"Cisplatin", "Citalopram", "Clobazam", "Clomiphene",
			"Clomipramine", "Clopidogrel", "Clozapine", "Codeine",
			"Crizotinib", "Dapsone", "Dasatinib", "Denileukin_Diftitox",
			"Desipramine", "Dexlansoprazole", "Dextromethorphan_and_Quinidine",
			"Diazepam", "Doxepin", "Drospirenone_and_Estradiol", "Erlotinib",
			"Esomeprazole", "Exemestane", "Everolimus", "Fluorouracil",
			"Fluoxetine", "Fluoxetine_and_Olanzapine", "Flurbiprofen",
			"Fluvoxamine", "Fulvestrant", "Galantamine", "Iloperidone",
			"Imatinib", "Imipramine", "Indacaterol", "Irinotecan",
			"Isosorbide", "Ivacaftor", "Lapatinib", "Lenalidomide",
			"Letrozole", "Maraviroc", "Mercaptopurine", "Metoprolol",
			"Modafinil", "Nefazodone", "Nilotinib", "Nortriptyline",
			"Omeprazole", "Panitumumab", "Pantoprazole", "Paroxetine",
			"Peginterferon_alfa-2b", "Perphenazine", "Pertuzumab", "Phenytoin",
			"Pimozide", "Prasugrel", "Pravastatin", "Propafenone",
			"Propranolol", "Protriptyline", "Quinidine", "Rabeprazole",
			"Rasburicase", "Rifampin", "Isoniazid", "Pyrazinamide",
			"Risperidone", "Sodium_Phenylacetate", "Sodium_Benzoate",
			"Sodium_Phenylbutyrate", "Tamoxifen", "Telaprevir", "Terbinafine",
			"Tetrabenazine", "Thioguanine", "Thioridazine", "Ticagrelor",
			"Tolterodine", "Tositumomab", "Tramadol_and_Acetaminophen",
			"Trastuzumab", "Tretinoin", "Trimipramine", "Valproic_Acid",
			"Vemurafenib", "Venlafaxine", "Voriconazole", "Warfarin" };

	// Paolo this is taking care of the 'PK impact' for 'apply'
	// I Normally create a method for each group
	// RadioButton groups return MLinkedResource
	// CheckBoxes groups return Set<MLinkedResource>
	// See FAntibodyForm as an example

	// drug of interest
	public MLinkedResource getDrugOfInterest() {

		int indexdoi = descriptdoi.getSelectedIndex();

		return ResourcesFactory.createTrustedTypedResource(SPL_POC_PREFIX + "",
				descriptdoi.getItemText(indexdoi), "the drug of interest.",
				SPL_POC_PREFIX + "Drug of Interest", SPL_POC_PREFIX,
				"U of Pitt SPL Pharmgx Annotation");
	}

	// biomarkers
	public MLinkedResource getBioMarkers() {

		int indexbm = descriptbm.getSelectedIndex();

		return ResourcesFactory.createTrustedTypedResource(SPL_POC_PREFIX
				+ "biomarkers", descriptbm.getItemText(indexbm),
				"the biomarkers.", SPL_POC_PREFIX + "Biomarkers",
				SPL_POC_PREFIX, "U of Pitt SPL Pharmgx Annotation");
	}

	// pk impact

	public MLinkedResource getPkImpact() {
		if (descriptpkia.getValue()) {
			return ResourcesFactory
					.createTrustedTypedResource(
							SPL_POC_PREFIX + "absorption-increase",
							"Absorption Increase",
							"The pharmacogenomic biomarker is associated with a increase in absorption of the drug.",
							SPL_POC_PREFIX + "PharmacokineticImpact",
							SPL_POC_PREFIX, "U of Pitt SPL Pharmgx Annotation");
		} else if (descriptpkda.getValue()) {
			return ResourcesFactory
					.createTrustedTypedResource(
							SPL_POC_PREFIX + "absorption-decrease",
							"Absorption Decrease",
							"The pharmacogenomic biomarker is associated with an decrease in absorption of the drug.",
							SPL_POC_PREFIX + "PharmacokineticImpact",
							SPL_POC_PREFIX, "U of Pitt SPL Pharmgx Annotation");
		} else if (descriptpkid.getValue()) {
			return ResourcesFactory
					.createTrustedTypedResource(
							SPL_POC_PREFIX + "distribution-increase",
							"Distribution Increase",
							"The pharmacogenomic biomarker is associated with a increase in distribution of the drug.",
							SPL_POC_PREFIX + "PharmacokineticImpact",
							SPL_POC_PREFIX, "U of Pitt SPL Pharmgx Annotation");
		} else if (descriptpkdd.getValue()) {
			return ResourcesFactory
					.createTrustedTypedResource(
							SPL_POC_PREFIX + "distribution-decrease",
							"Distribution Decrease",
							"The pharmacogenomic biomarker is associated with an decrease in distribution of the drug.",
							SPL_POC_PREFIX + "PharmacokineticImpact",
							SPL_POC_PREFIX, "U of Pitt SPL Pharmgx Annotation");
		} else if (descriptpkim.getValue()) {
			return ResourcesFactory
					.createTrustedTypedResource(
							SPL_POC_PREFIX + "metabolism-increase",
							"Metabolism Increase",
							"The pharmacogenomic biomarker is associated with an increase in metabolism of the drug.",
							SPL_POC_PREFIX + "PharmacokineticImpact",
							SPL_POC_PREFIX, "U of Pitt SPL Pharmgx Annotation");
		} else if (descriptpkdm.getValue()) {
			return ResourcesFactory
					.createTrustedTypedResource(
							SPL_POC_PREFIX + "metabolism-decrease",
							"Metabolism Decrease",
							"The pharmacogenomic biomarker is associated with a decrease in metabolism of the drug.",
							SPL_POC_PREFIX + "PharmacokineticImpact",
							SPL_POC_PREFIX, "U of Pitt SPL Pharmgx Annotation");
		} else if (descriptpkie.getValue()) {
			return ResourcesFactory
					.createTrustedTypedResource(
							SPL_POC_PREFIX + "excretion-increase",
							"Excretion Increase",
							"*************************** this was missing ************************",
							SPL_POC_PREFIX + "PharmacokineticImpact",
							SPL_POC_PREFIX, "U of Pitt SPL Pharmgx Annotation");
		} else if (descriptpkde.getValue()) {
			return ResourcesFactory
					.createTrustedTypedResource(
							SPL_POC_PREFIX + "excretion-decrease",
							"Excretion Decrease",
							"*************************** this was missing ************************",
							SPL_POC_PREFIX + "PharmacokineticImpact",
							SPL_POC_PREFIX, "U of Pitt SPL Pharmgx Annotation");
		} else if (descriptpkni.getValue()) {
			return ResourcesFactory
					.createTrustedTypedResource(
							SPL_POC_PREFIX + "not-important",
							"Not Important",
							"The pharmacogenomic biomarker is not associated any clinically relevant pharmacokinetic with respect to the drug.",
							SPL_POC_PREFIX + "PharmacokineticImpact",
							SPL_POC_PREFIX, "U of Pitt SPL Pharmgx Annotation");

		} else if (descriptpknone.getValue()) {
			return ResourcesFactory.createTrustedTypedResource(SPL_POC_PREFIX
					+ "none", "None", "none", SPL_POC_PREFIX
					+ "PharmacokineticImpact", SPL_POC_PREFIX,
					"U of Pitt SPL Pharmgx Annotation");
		}

		return null;
	}

	// Pharmacodynamic impact PD

	public MLinkedResource getPdImpact() {

		if (descriptpddt.getValue()) {
			return ResourcesFactory
					.createTrustedTypedResource(
							SPL_POC_PREFIX + "drug-toxicity-risk-decreased",
							"Decreased Toxicity Risk",
							"The pharmacogenomic biomarker is associated with an decreased risk of toxicity.",
							SPL_POC_PREFIX + "PharmacodynamicImpact",
							SPL_POC_PREFIX, "U of Pitt SPL Pharmgx Annotation");
		} else if (descriptpdit.getValue()) {
			return ResourcesFactory
					.createTrustedTypedResource(
							SPL_POC_PREFIX + "drug-toxicity-risk-increased",
							"Increased Toxicity Risk",
							"The pharmacogenomic biomarker is associated with an increased risk of toxicity.",
							SPL_POC_PREFIX + "PharmacodynamicImpact",
							SPL_POC_PREFIX, "U of Pitt SPL Pharmgx Annotation");
		} else if (descriptpdir.getValue()) {
			return ResourcesFactory.createTrustedTypedResource(SPL_POC_PREFIX
					+ "influences-drug-response", "Influences Drug Response",
					"The pharmacogenomic biomarker influences drug response",
					SPL_POC_PREFIX + "PharmacodynamicImpact", SPL_POC_PREFIX,
					"U of Pitt SPL Pharmgx Annotation");
		} else if (descriptpdni.getValue()) {
			return ResourcesFactory
					.createTrustedTypedResource(
							SPL_POC_PREFIX + "not-important",
							"Not Important",
							"The pharmacogenomic biomarker is not associated with clinically relevant pharmacodynamic effect",
							SPL_POC_PREFIX + "PharmacodynamicImpact",
							SPL_POC_PREFIX, "U of Pitt SPL Pharmgx Annotation");
		} else if (descriptpdie.getValue()) {
			return ResourcesFactory
					.createTrustedTypedResource(
							SPL_POC_PREFIX
									+ "drug-efficacy-increased-from-baseline",
							"Increased Efficacy",
							"The pharmacogenomic biomarker is associated with an increase in the efficacy of the drug. ",
							SPL_POC_PREFIX + "PharmacodynamicImpact",
							SPL_POC_PREFIX, "U of Pitt SPL Pharmgx Annotation");
		} else if (descriptpdde.getValue()) {
			return ResourcesFactory
					.createTrustedTypedResource(
							SPL_POC_PREFIX
									+ "drug-efficacy-decreased-from-baseline",
							"Decreased Efficacy",
							"The pharmacogenomic biomarker is associated with an decrease in the efficacy of the drug.",
							SPL_POC_PREFIX + "PharmacodynamicImpact",
							SPL_POC_PREFIX, "U of Pitt SPL Pharmgx Annotation");
		} else if (descriptpdnone.getValue()) {
			return ResourcesFactory.createTrustedTypedResource(SPL_POC_PREFIX
					+ "none", "None", "none.", SPL_POC_PREFIX
					+ "PharmacodynamicImpact", SPL_POC_PREFIX,
					"U of Pitt SPL Pharmgx Annotation");
		}
		return null;
	}

	// Recommendation drug

	public MLinkedResource getDrugRec() {

		if (descriptdsal.getValue()) {
			return ResourcesFactory
					.createTrustedTypedResource(
							SPL_POC_PREFIX + "alternative-recommended",
							"Alternative Recommended",
							"The pharmacogenomic biomarker is related to a recommendation to use an alternative drug.",
							SPL_POC_PREFIX + "DrugSelectionRecommendation",
							SPL_POC_PREFIX, "U of Pitt SPL Pharmgx Annotation");
		} else if (descriptdsnr.getValue()) {
			return ResourcesFactory
					.createTrustedTypedResource(
							SPL_POC_PREFIX + "do-not-restart",
							"Do not restart",
							"The pharmacogenomic biomarker is related to a recommendation to not restart the drug",
							SPL_POC_PREFIX + "DrugSelectionRecommendation",
							SPL_POC_PREFIX, "U of Pitt SPL Pharmgx Annotation");
		} else if (descriptdsnc.getValue()) {
			return ResourcesFactory
					.createTrustedTypedResource(
							SPL_POC_PREFIX + "no-change-necessary",
							"Not change necessary",
							"The pharmacogenomic biomarker is not associated with any drug selection recommendation.",
							SPL_POC_PREFIX + "DrugSelectionRecommendation",
							SPL_POC_PREFIX, "U of Pitt SPL Pharmgx Annotation");
		} else if (descriptdsam.getValue()) {
			return ResourcesFactory
					.createTrustedTypedResource(
							SPL_POC_PREFIX + "addition-of-medication",
							"Addition of medication",
							"The pharmacogenomic biomarker is related to a recommendation to add a concomitant medication.",
							SPL_POC_PREFIX + "DrugSelectionRecommendation",
							SPL_POC_PREFIX, "U of Pitt SPL Pharmgx Annotation");
		} else if (descriptdsca.getValue()) {
			return ResourcesFactory
					.createTrustedTypedResource(
							SPL_POC_PREFIX + "change-in-route-of-admin",
							"Change in route of administration",
							"The pharmacogenomic biomarker is related to a recommendation to add change the route of administration for the drug.",
							SPL_POC_PREFIX + "DrugSelectionRecommendation",
							SPL_POC_PREFIX, "U of Pitt SPL Pharmgx Annotation");
		} else if (descriptdsnone.getValue()) {
			return ResourcesFactory.createTrustedTypedResource(SPL_POC_PREFIX
					+ "none", "None", "none.", SPL_POC_PREFIX
					+ "DrugSelectionRecommendation", SPL_POC_PREFIX,
					"U of Pitt SPL Pharmgx Annotation");
		}
		return null;
	}

	// Recommendation Dose

	public MLinkedResource getDoseRec() {

		if (descriptdrdfb.getValue()) {
			return ResourcesFactory
					.createTrustedTypedResource(
							SPL_POC_PREFIX
									+ "decrease-from-recommended-baseline",
							"Decrease from baseline",
							"The pharmacogenomic biomarker is related to a recommendation to decrease the dose of the drug from the recommended baseline.",
							SPL_POC_PREFIX + "DoseSelectionRecommendation",
							SPL_POC_PREFIX, "U of Pitt SPL Pharmgx Annotation");
		} else if (descriptdrifb.getValue()) {
			return ResourcesFactory
					.createTrustedTypedResource(
							SPL_POC_PREFIX
									+ "increase-from-recommended-baseline",
							"Increase from baseline",
							"The pharmacogenomic biomarker is related to a recommendation to increase the dose of the drug from the recommended baseline.",
							SPL_POC_PREFIX + "DoseSelectionRecommendation",
							SPL_POC_PREFIX, "U of Pitt SPL Pharmgx Annotation");
		} else if (descriptdrnc.getValue()) {
			return ResourcesFactory
					.createTrustedTypedResource(
							SPL_POC_PREFIX
									+ "not-change-from-recommended-baseline",
							"Not change from baseline",
							"The pharmacogenomic biomarker is related to a recommendation to not change the dose of the drug from the recommended baseline.",
							SPL_POC_PREFIX + "DoseSelectionRecommendation",
							SPL_POC_PREFIX, "U of Pitt SPL Pharmgx Annotation");
		} else if (descriptdrus.getValue()) {
			return ResourcesFactory
					.createTrustedTypedResource(
							SPL_POC_PREFIX + "use-specific",
							"Use specific",
							"The pharmacogenomic biomarker is related to a recommendation to use specific dose of the drug from the recommended baseline.",
							SPL_POC_PREFIX + "DoseSelectionRecommendation",
							SPL_POC_PREFIX, "U of Pitt SPL Pharmgx Annotation");
		} else if (descriptdrcs.getValue()) {
			return ResourcesFactory
					.createTrustedTypedResource(
							SPL_POC_PREFIX + "change-schedule",
							"Change schedule",
							"The pharmacogenomic biomarker is related to a recommendation to change schedule of the dose of the drug from the recommended baseline.",
							SPL_POC_PREFIX + "DoseSelectionRecommendation",
							SPL_POC_PREFIX, "U of Pitt SPL Pharmgx Annotation");
		} else if (descriptdrnone.getValue()) {
			return ResourcesFactory.createTrustedTypedResource(SPL_POC_PREFIX
					+ "none", "None", "none.", SPL_POC_PREFIX
					+ "DoseSelectionRecommendation", SPL_POC_PREFIX,
					"U of Pitt SPL Pharmgx Annotation");
		}
		return null;
	}

	// Recommendation Monitoring

	public MLinkedResource getMonitRec() {

		if (descriptmreq.getValue()) {
			return ResourcesFactory
					.createTrustedTypedResource(
							SPL_POC_PREFIX + "required",
							"Required",
							"A required monitoring recommendation is related to the pharmacogenomic biomarker.",
							SPL_POC_PREFIX + "MonitoringRecommendation",
							SPL_POC_PREFIX, "U of Pitt SPL Pharmgx Annotation");
		} else if (descriptmrec.getValue()) {
			return ResourcesFactory
					.createTrustedTypedResource(
							SPL_POC_PREFIX + "recommended",
							"Recommended",
							"A recommended monitoring recommendation is related to the pharmacogenomic biomarker.",
							SPL_POC_PREFIX + "MonitoringRecommendation",
							SPL_POC_PREFIX, "U of Pitt SPL Pharmgx Annotation");
		} else if (descriptmnc.getValue()) {
			return ResourcesFactory
					.createTrustedTypedResource(
							SPL_POC_PREFIX + "not-necessary",
							"Not necessary",
							"A not necessary monitoring recommendation is related to the pharmacogenomic biomarker.",
							SPL_POC_PREFIX + "MonitoringRecommendation",
							SPL_POC_PREFIX, "U of Pitt SPL Pharmgx Annotation");
		} else if (descriptmcms.getValue()) {
			return ResourcesFactory
					.createTrustedTypedResource(
							SPL_POC_PREFIX + "change-monitoring-strategy",
							"Change monitoring strategy",
							"A strategy changed monitoring recommendation is related to the pharmacogenomic biomarker.",
							SPL_POC_PREFIX + "MonitoringRecommendation",
							SPL_POC_PREFIX, "U of Pitt SPL Pharmgx Annotation");
		} else if (descriptmcnone.getValue()) {
			return ResourcesFactory.createTrustedTypedResource(SPL_POC_PREFIX
					+ "none", "None", "none.", SPL_POC_PREFIX
					+ "MonitoringRecommendation", SPL_POC_PREFIX,
					"U of Pitt SPL Pharmgx Annotation");
		}
		return null;
	}

	// test section

	public MLinkedResource getTest_Re() {
		if (descripttreq.getValue()) {
			return ResourcesFactory.createTrustedTypedResource(SPL_POC_PREFIX
					+ "required", "Required",
					"A required test is related to the biomarker.",
					SPL_POC_PREFIX + "TestRecommendation", SPL_POC_PREFIX,
					"U of Pitt SPL Pharmgx Annotation");
		} else if (descripttrec.getValue()) {
			return ResourcesFactory.createTrustedTypedResource(SPL_POC_PREFIX
					+ "recommend", "Recommend",
					"A recommended test is related to the biomarker.",
					SPL_POC_PREFIX + "TestRecommendation", SPL_POC_PREFIX,
					"U of Pitt SPL Pharmgx Annotation");
		}

		else if (descriptttna.getValue()) {
			return ResourcesFactory
					.createTrustedTypedResource(
							SPL_POC_PREFIX
									+ "take-note-that-tests-are-avaliable",
							"Take note that tests are avaliable",
							"Testing related to the pharmacogenomic biomarker is avaliable.",
							SPL_POC_PREFIX + "TestRecommendation",
							SPL_POC_PREFIX, "U of Pitt SPL Pharmgx Annotation");
		}

		else if (descripttnn.getValue()) {
			return ResourcesFactory
					.createTrustedTypedResource(
							SPL_POC_PREFIX + "not-necessary",
							"Not necessary",
							"Testing related to the pharmacogenomic biomarker is not necessary.",
							SPL_POC_PREFIX + "TestRecommendation",
							SPL_POC_PREFIX, "U of Pitt SPL Pharmgx Annotation");
		} else if (descripttnone.getValue()) {
			return ResourcesFactory.createTrustedTypedResource(SPL_POC_PREFIX
					+ "none", "None", "none.", SPL_POC_PREFIX
					+ "TestRecommendation", SPL_POC_PREFIX,
					"U of Pitt SPL Pharmgx Annotation");
		}
		return null;
	}

	// statements
	// the value of each variables is made up
	public Set<MLinkedResource> getStatements() {

		Set<MLinkedResource> statements = new HashSet<MLinkedResource>();

		if (descriptsai.getValue()) {
			statements.add(ResourcesFactory.createTrustedTypedResource(
					SPL_POC_PREFIX + "ingredient-active", "Active ingredient",
					"the ingredient is active", SPL_POC_PREFIX + "Statements",
					SPL_POC_PREFIX, "U of Pitt SPL Pharmgx Annotation"));
		}

		if (descriptsmcc.getValue()) {
			statements.add(ResourcesFactory.createTrustedTypedResource(
					SPL_POC_PREFIX + "concomitant-medication-concern",
					"Concomitant medication concern",
					"we should concern the concomitant medication.",
					SPL_POC_PREFIX + "Statements", SPL_POC_PREFIX,
					"U of Pitt SPL Pharmgx Annotation"));
		}

		if (descriptppm.getValue()) {

			statements.add(ResourcesFactory.createTrustedTypedResource(
					SPL_POC_PREFIX + "", "Variant Frequency",
					"variant frequency", SPL_POC_PREFIX + "Statements",
					SPL_POC_PREFIX, "U of Pitt SPL Pharmgx Annotation"));
		}

		return statements;
	}

	// Variant
	public MLinkedResource getVarient() {

		int indexbm = descriptsvtlb.getSelectedIndex();

		return ResourcesFactory.createTrustedTypedResource(SPL_POC_PREFIX
				+ "variant", descriptsvtlb.getItemText(indexbm),
				"it is highly possible changes with time.", SPL_POC_PREFIX
						+ "Variant", SPL_POC_PREFIX,
				"U of Pitt SPL Pharmgx Annotation");
	}

	// Test
	public MLinkedResource getTest() {

		int indexbm = descriptstslb.getSelectedIndex();

		return ResourcesFactory.createTrustedTypedResource(SPL_POC_PREFIX
				+ "test", descriptstslb.getItemText(indexbm),
				"based on the test result. ", SPL_POC_PREFIX + "Test",
				SPL_POC_PREFIX, "U of Pitt SPL Pharmgx Annotation");
	}

	// NEW annotation
	@SuppressWarnings("deprecation")
	public FSPLsForm(IDomeo domeo, final AFormsManager manager) {
		super(domeo);
		_manager = manager;

		initWidget(binder.createAndBindUi(this));

		_domeo.getLogger().debug(this, "SPL annotation widget bound to UI");

		refreshAnnotationSetFilter(annotationSet, null);

		_domeo.getLogger().debug(this, "SPL annotation filter set refreshed");

		ButtonWithIcon yesButton = new ButtonWithIcon(Domeo.resources
				.generalCss().applyButton());
		yesButton.setWidth("78px");
		yesButton.setHeight("22px");
		yesButton.setResource(Domeo.resources.acceptLittleIcon());
		yesButton.setText("Apply");
		yesButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (isContentInvalid())
					return; // TODO: use this function to validate form elements
							// in the UI

				_domeo.getLogger().debug(this,
						"SPL annotation content validated");

				try {
					if (_item == null) {
						_domeo.getLogger().debug(this, "_item is null...");
						if (_manager instanceof TextAnnotationFormsPanel) {
							MTextQuoteSelector selector = AnnotationFactory
									.createPrefixSuffixTextSelector(
											_domeo.getAgentManager()
													.getUserPerson(),
											_domeo.getPersistenceManager()
													.getCurrentResource(),
											((TextAnnotationFormsPanel) _manager)
													.getHighlight().getExact(),
											((TextAnnotationFormsPanel) _manager)
													.getHighlight().getPrefix(),
											((TextAnnotationFormsPanel) _manager)
													.getHighlight().getSuffix());

							_domeo.getLogger().debug(this,
									"quote selector initialized");

							// TODO Register coordinate of the selection.
							MSPLsAnnotation annotation = SPLsFactory
									.createSPLsAnnotation(
											((AnnotationPersistenceManager) _domeo
													.getPersistenceManager())
													.getCurrentSet(), _domeo
													.getAgentManager()
													.getUserPerson(), _domeo
													.getAgentManager()
													.getSoftware(), _manager
													.getResource(), selector);

							_domeo.getLogger().debug(this,
									"SPL annotation factory initialized");

							MSPLPharmgxUsage pharmgxUsage = SPLsFactory
									.createSPLPharmgxUsage();

							// take the form values and assign
							_domeo.getLogger().debug(this, "SPL annotation 1");
							pharmgxUsage.setBiomarkers(getBioMarkers());
							_domeo.getLogger().debug(this, "SPL annotation 2");
							pharmgxUsage.setPkImpact(getPkImpact());
							_domeo.getLogger().debug(this, "SPL annotation 3");
							pharmgxUsage.setPdImpact(getPdImpact());
							_domeo.getLogger().debug(this, "SPL annotation 4");
							pharmgxUsage.setDrugRec(getDrugRec());
							_domeo.getLogger().debug(this, "SPL annotation 5");
							pharmgxUsage.setDoseRec(getDoseRec());
							_domeo.getLogger().debug(this, "SPL annotation 6");
							pharmgxUsage.setMonitRec(getMonitRec());
							_domeo.getLogger().debug(this, "SPL annotation 7");
							pharmgxUsage.setStatements(getStatements());
							_domeo.getLogger().debug(this, "SPL annotation 8");
							pharmgxUsage.setTestRec(getTest_Re());
							_domeo.getLogger().debug(this, "SPL annotation 9");
							pharmgxUsage.setAllelesbody(allelesbody.getText());
							_domeo.getLogger().debug(this, "SPL annotation 10");
							pharmgxUsage.setMedconditbody(medconditbody
									.getText());
							pharmgxUsage.setVarient(getVarient());
							pharmgxUsage.setTest(getTest());
							pharmgxUsage.setDrugOfInterest(getDrugOfInterest());
							pharmgxUsage.setVarientbody(descriptsvtbody
									.getText());
							pharmgxUsage.setTestbody(descriptstsbody.getText());

							annotation.setPharmgxUsage(pharmgxUsage);

							_domeo.getLogger()
									.debug(this,
											"SPL pharmgxUsage initialized and attached to the SPL annotation instance");

							// _domeo.getLogger().command(getLogCategoryCreate(),
							// this, " with term " + currentPharmgx.getLabel());

							annotation.setComment(commentBody.getText());
							_domeo.getLogger().debug(this, "SPL comment set");

							if (getSelectedSet(annotationSet) == null) {
								_domeo.getLogger()
										.debug(this,
												"empty annotation set, passing first SPL annotation to persistance manager");
								_domeo.getAnnotationPersistenceManager()
										.addAnnotation(annotation, true);
							} else {
								_domeo.getLogger()
										.debug(this,
												"Annotation set is not empty, passing new SPL annotation to persistance manager");
								_domeo.getAnnotationPersistenceManager()
										.addAnnotation(annotation,
												getSelectedSet(annotationSet));
							}

							_domeo.getLogger()
									.debug(this,
											"Making the new SPL annotation visible in the content panel");
							_domeo.getContentPanel()
									.getAnnotationFrameWrapper()
									.performAnnotation(
											annotation,
											((TextAnnotationFormsPanel) _manager)
													.getHighlight());

							_domeo.getLogger()
									.debug(this,
											"SPL annotation data collected. widget should close now.");

							_manager.hideContainer();

						}
					} else {
						// _item.setType(PostitType.findByName(postitTypes.getItemText(postitTypes.getSelectedIndex())));
						// _item.setText(getPostItBody());
						_domeo.getLogger().debug(this, "_item is NOT null...");

						_manager.hideContainer();
					}
				} catch (Exception e) {
					logger.log(Level.WARNING,
							"throwing exception " + e.getMessage());
					_domeo.getLogger().exception(this, e.getMessage());
				}
			}
		});
		buttonsPanel.add(yesButton);

		_domeo.getLogger().debug(this, "'Apply' button added");

		this.setHeight("100px");

		// rightColumn.add(tabs.get(0));
		// resized();
	}

	// ------------------------
	// EDITING OF ANNOTATION
	// ------------------------
	public FSPLsForm(IDomeo domeo, final AFormsManager manager,
			final MSPLsAnnotation annotation) {
		super(domeo);
		_manager = manager;
		_item = annotation;

		initWidget(binder.createAndBindUi(this));

		_domeo.getLogger().debug(this,
				"SPL annotation widget bound to UI (edit)");

		try {
			_domeo.getLogger()
					.debug(this,
							"Attempting to initialize UI with loaded annotation values");

			if (_item.getComment() != null && !_item.getComment().equals(""))
				commentBody.setText(_item.getComment());

			// drug of interest

			if (_item.getDrugOfInterest() != null) {
				if (_item.getDrugOfInterest().getLabel().equals("unselected")) {
					descriptdoi.setSelectedIndex(0);
				}

				String drugOfInterest = _item.getDrugOfInterest().getLabel();
				for (int i = 0; i < drugs.length; i++) {
					if (drugs[i].equals(drugOfInterest)) {
						descriptdoi.setSelectedIndex(i + 1);
					}
				}
			}

			// set biomarkers
			if (_item.getBiomarkers() != null) {

				if (_item.getBiomarkers().getLabel().equals("unselected")) {
					descriptbm.setSelectedIndex(0);
				}

				String bioLabel = _item.getBiomarkers().getLabel();
				for (int i = 0; i < biomarker.length; i++) {
					if (biomarker[i].equals(bioLabel)) {
						descriptbm.setSelectedIndex(i + 1);
					}
				}
			}

			if (_item.getPKImpact() != null) {
				if (_item.getPKImpact().getLabel()
						.equals("Metabolism Decrease"))
					descriptpkdm.setValue(true);
				else if (_item.getPKImpact().getLabel()
						.equals("Metabolism Increase"))
					descriptpkim.setValue(true);
				else if (_item.getPKImpact().getLabel()
						.equals("Absorption Increase"))
					descriptpkia.setValue(true);
				else if (_item.getPKImpact().getLabel()
						.equals("Absorption Decrease"))
					descriptpkda.setValue(true);
				else if (_item.getPKImpact().getLabel()
						.equals("Distribution Increase"))
					descriptpkid.setValue(true);
				else if (_item.getPKImpact().getLabel()
						.equals("Distribution Decrease"))
					descriptpkdd.setValue(true);
				else if (_item.getPKImpact().getLabel()
						.equals("Excretion Increase"))
					descriptpkie.setValue(true);
				else if (_item.getPKImpact().getLabel()
						.equals("Excretion Decrease"))
					descriptpkde.setValue(true);
				else if (_item.getPKImpact().getLabel().equals("Not Important"))
					descriptpkni.setValue(true);
				else if (_item.getPKImpact().getLabel().equals("None"))
					descriptpknone.setValue(true);
			}

			if (_item.getPdImpact() != null) {
				if (_item.getPdImpact().getLabel()
						.equals("Decreased Toxicity Risk"))
					descriptpddt.setValue(true);
				else if (_item.getPdImpact().getLabel()
						.equals("Increased Toxicity Risk"))
					descriptpdit.setValue(true);
				else if (_item.getPdImpact().getLabel()
						.equals("Influences Drug Response"))
					descriptpdir.setValue(true);
				else if (_item.getPdImpact().getLabel().equals("Not Important"))
					descriptpdni.setValue(true);
				else if (_item.getPdImpact().getLabel()
						.equals("Increased Efficacy"))
					descriptpdie.setValue(true);
				else if (_item.getPdImpact().getLabel()
						.equals("Decreased Efficacy"))
					descriptpdde.setValue(true);
				else if (_item.getPdImpact().getLabel().equals("None"))
					descriptpdnone.setValue(true);
			}

			if (_item.getDoseRec() != null) {
				if (_item.getDoseRec().getLabel()
						.equals("Decrease from baseline"))
					descriptdrdfb.setValue(true);
				else if (_item.getDoseRec().getLabel()
						.equals("Increase from baseline"))
					descriptdrifb.setValue(true);
				else if (_item.getDoseRec().getLabel()
						.equals("Not change from baseline"))
					descriptdrnc.setValue(true);
				else if (_item.getDoseRec().getLabel().equals("Use specific"))
					descriptdrus.setValue(true);
				else if (_item.getDoseRec().getLabel()
						.equals("Change schedule"))
					descriptdrcs.setValue(true);
				else if (_item.getDoseRec().getLabel().equals("None"))
					descriptdrnone.setValue(true);
			}

			if (_item.getMonitRec() != null) {
				if (_item.getMonitRec().getLabel().equals("Required"))
					descriptmreq.setValue(true);
				else if (_item.getMonitRec().getLabel().equals("Recommended"))
					descriptmrec.setValue(true);
				else if (_item.getMonitRec().getLabel().equals("Not necessary"))
					descriptmnc.setValue(true);
				else if (_item.getMonitRec().getLabel()
						.equals("Change monitoring strategy"))
					descriptmcms.setValue(true);
				else if (_item.getMonitRec().getLabel().equals("None"))
					descriptmcnone.setValue(true);
			}

			if (_item.getDrugRec() != null) {

				if (_item.getDrugRec().getLabel()
						.equals("Alternative Recommended"))
					descriptdsal.setValue(true);
				else if (_item.getDrugRec().getLabel()
						.equals("Change in route of administration"))
					descriptdsca.setValue(true);
				else if (_item.getDrugRec().getLabel().equals("Add medication"))
					descriptdsam.setValue(true);
				else if (_item.getDrugRec().getLabel().equals("Do not restart"))
					descriptdsnr.setValue(true);
				else if (_item.getDrugRec().getLabel()
						.equals("Not change necessary"))
					descriptdsnc.setValue(true);
				else if (_item.getDrugRec().getLabel().equals("None"))
					descriptdsnone.setValue(true);
			}

			if (_item.getTestRec() != null) {

				if (_item.getTestRec().getLabel().equals("Required"))
					descripttreq.setValue(true);
				else if (_item.getTestRec().getLabel().equals("Recommend"))
					descripttrec.setValue(true);
				else if (_item.getTestRec().getLabel()
						.equals("Take note that tests are avaliable"))
					descriptttna.setValue(true);
				else if (_item.getTestRec().getLabel().equals("Not necessary"))
					descripttnn.setValue(true);
				else if (_item.getTestRec().getLabel().equals("None"))
					descripttnone.setValue(true);
			}

			// statements, values are made up
			// checkbox

			if (_item.getStatements() != null) {
				for (MLinkedResource statemt : _item.getStatements()) {

					if (statemt.getLabel().equals("Active ingredient")) {
						descriptsai.setValue(true);
					}
					if (statemt.getLabel().equals(
							"Concomitant medication concern")) {
						descriptsmcc.setValue(true);
					}

					if (statemt.getLabel().equals("Variant Frequency")) {
						descriptppm.setValue(true);
					}

				}
			}

			// Variant

			if (_item.getVarient() != null) {

				if (_item.getVarient().getLabel().equals("unselected")) {
					descriptsvtlb.setSelectedIndex(0);
				}

				String varientLabel = _item.getVarient().getLabel();
				for (int i = 0; i < varient.length; i++) {
					if (varient[i].equals(varientLabel)) {
						descriptsvtlb.setSelectedIndex(i + 1);
					}
				}
			}

			// Test

			if (_item.getTest() != null) {

				if (_item.getTest().getLabel().equals("unselected")) {
					descriptstslb.setSelectedIndex(0);
				}

				String testLabel = _item.getTest().getLabel();
				for (int i = 0; i < test.length; i++) {
					if (test[i].equals(testLabel)) {
						descriptstslb.setSelectedIndex(i + 1);
					}
				}
			}

			// alleles body

			if (_item.getAllelesbody() != null) {
				allelesbody.setText(_item.getAllelesbody());
			}

			// medical condition
			if (_item.getMedconditbody() != null) {
				medconditbody.setText(_item.getMedconditbody());
			}

			// varient textarea
			if (_item.getVarientBody() != null) {
				descriptsvtbody.setText(_item.getVarientBody());
			}

			// test textarea
			if (_item.getTestBody() != null) {
				descriptstsbody.setText(_item.getTestBody());
			}

		} catch (Exception e) {
			_domeo.getLogger().exception(
					this,
					"Failed to display current annotation "
							+ annotation.getLocalId());
			displayDialog(
					"Failed to properly display existing annotation "
							+ e.getMessage(), true);
		}

		try {
			refreshAnnotationSetFilter(annotationSet, annotation);

			_domeo.getLogger().debug(this,
					"SPL annotation filter set refreshed");

			currentPharmgx = annotation.getPharmgxUsage().getPharmgx();

			_domeo.getLogger()
					.debug(this,
							"acquired the pharmgx usage from the annotation instance passed to this method");
		} catch (Exception e) {
			_domeo.getLogger().exception(
					AnnotationFrameWrapper.LOG_CATEGORY_EDIT_ANNOTATION,
					this,
					"Failed to display current annotation "
							+ annotation.getLocalId());
			displayDialog(
					"Failed to properly display existing annotation "
							+ e.getMessage(), true);
		}

		ButtonWithIcon sameVersionButton = new ButtonWithIcon();
		sameVersionButton.setStyleName(Domeo.resources.generalCss()
				.applyButton());
		sameVersionButton.setWidth("78px");
		sameVersionButton.setHeight("22px");
		sameVersionButton.setResource(Domeo.resources.acceptLittleIcon());
		sameVersionButton.setText("Apply");
		sameVersionButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				System.out.println("onClick function triggered");
				try {
					if (isContentInvalid()) {
						return; // TODO: modify this function to validate our UI

					}

					MTextQuoteSelector selector = AnnotationFactory
							.createPrefixSuffixTextSelector(_domeo
									.getAgentManager().getUserPerson(), _domeo
									.getPersistenceManager()
									.getCurrentResource(),
									((TextAnnotationFormsPanel) _manager)
											.getHighlight().getExact(),
									((TextAnnotationFormsPanel) _manager)
											.getHighlight().getPrefix(),
									((TextAnnotationFormsPanel) _manager)
											.getHighlight().getSuffix());

					_item.setSelector(selector);

					_domeo.getLogger().debug(this,
							"SPL annotation content validated (edit)");

					_item.setComment(commentBody.getText());

					_item.setDrugOfInterest(getDrugOfInterest());
					_item.setBiomarkers(getBioMarkers());
					_item.setPKImpact(getPkImpact());
					_item.setPdImpact(getPdImpact());
					_item.setDrugRec(getDrugRec());
					_item.setDoseRec(getDoseRec());
					_item.setMonitRec(getMonitRec());
					_item.setStatements(getStatements());
					_item.setTestRec(getTest_Re());
					_item.setVarient(getVarient());
					_item.setTest(getTest());
					_item.setVarientBody(descriptsvtbody.getText());
					_item.setTestBody(descriptstsbody.getText());
					_item.setAllelesbody(allelesbody.getText());
					_item.setMedconditbody(medconditbody.getText());

					_domeo.getLogger().debug(this,
							"SPL descriptions cleared and re-loaded (edit)");

					_item.getPharmgxUsage().setPharmgx(currentPharmgx);

					_domeo.getContentPanel()
							.getAnnotationFrameWrapper()
							.updateAnnotation(_item,
									getSelectedSet(annotationSet));

					_domeo.getLogger()
							.debug(this,
									"SPL annotation data collected. widget should close now.");
					_manager.hideContainer();
				} catch (Exception e) {
					_domeo.getLogger()
							.exception(
									AnnotationFrameWrapper.LOG_CATEGORY_EDIT_ANNOTATION,
									this,
									"Failed to apply modified anntoation "
											+ annotation.getLocalId());
					displayDialog(
							"Failed to apply modified annotation "
									+ e.getMessage(), true);
				}
			}
		});
		buttonsPanel.add(sameVersionButton);

		this.setHeight("100px");

		// rightColumn.add(tabs.get(0));
		// resized();
	}

	public String getTitle() {
		return LABEL;
	}

	@Override
	public String getLogCategoryCreate() {
		return LOG_CATEGORY_QUALIFIER_CREATE;
	}

	@Override
	public String getLogCategoryEdit() {
		return LOG_CATEGORY_QUALIFIER_EDIT;
	}

	// TODO: edit this method to validate the relevant form elements
	@Override
	public boolean isContentInvalid() {
		// _manager.displayMessage("Click received!");
		// if(currentPharmgx==null) {
		// _manager.displayMessage("The body of the annotation cannot be empty!");
		// Timer timer = new Timer() {
		// public void run() {
		// _manager.clearMessage();
		// }
		// };
		// timer.schedule(2000);
		// return true;
		// }
		return false;
	}

	public boolean areMethodsChanged() {
		// if(_item.getSioDescriptions().size()!=getMethods().size()) return
		// true;
		// TODO check items
		return false;
	}

	@Override
	public void resized() {
		/*
		 * this.setWidth((Window.getClientWidth() - 340) + "px");
		 * tabBar.setWidth((Window.getClientWidth() - 615) + "px"); for (Widget
		 * tab : tabs) { // if(tab instanceof IResizable)
		 * ((IResizable)tab).resized(); tab.setWidth((Window.getClientWidth() -
		 * 615) + "px"); }
		 */
	}

	@Override
	public boolean isContentChanged(MAnnotation annotation) {
		// TODO Auto-generated method stub
		return false;
	}

	// @Override
	// public void addPharmgx(MPharmgx pharmgx) {
	// addAssociatedAntibody(pharmgx);
	// }
}
