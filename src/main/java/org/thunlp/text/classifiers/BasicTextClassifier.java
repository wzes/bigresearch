package org.thunlp.text.classifiers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.thunlp.io.TextFileReader;
import org.thunlp.io.TextFileWriter;
import org.thunlp.language.chinese.LangUtils;

import com.monetware.model.analysis.AnalysisProjectClassifyTraining;


public class BasicTextClassifier{

	/**
	 * 是否已读取模型
	 */
	private boolean modelLoaded = false;
	/**
	 * 词典大小
	 */
	private int lexiconSize = 0;
	/**
	 * 返回的可能分类数目
	 */
	protected int resultNum = 1;
	/**
	 * 分类器接口
	 */
	protected TextClassifier classifier;
	/**
	 * 类别列表
	 */
	protected final ArrayList<String> categoryList = new ArrayList<String>();

	protected int index = 0;
	/**
	 * 训练语料路径
	 */
	protected String trainingFolder = null;
	/**
	 * 测试语料路径
	 */
	protected String testingFolder = null;
	/**
	 * 读取模型路径
	 */
	protected String loadModelPath = null;
	/**
	 * 保存模型路径
	 */
	protected String saveModelPath = null;
	/**
	 * 分类器应用路径
	 */
	protected String classifyPath = null;
	/**
	 * svm使用liblinear
	 */
	protected boolean linear = true;
	/**
	 * 训练目录编号
	 */
	protected int trainingPathIndex = -1;
	/**
	 * 测试目录编号
	 */
	protected int testingPathIndex = -1;
	/**
	 * 训练文件使用比例
	 */
	protected double ratio1 = 0.8;
	/**
	 * 测试文件使用比例
	 */
	protected double ratio2 = 0.2;
	



	/**
	 * 最大特征数
	 */
	protected int maxFeatures = -1;
	/**
	 * 文件编码
	 */
	protected String encoding = "utf-8";
	/**
	 * 后缀过滤
	 */
	protected String suffix = null;
	/**
	 * 是否打印细节
	 */
	protected boolean printDetail = false;
	/**
	 * 分类编号索引
	 */
	public Hashtable<String, Integer> categoryToInt = new Hashtable<String, Integer>();
	/**
	 * 测试器
	 */
	protected Tester tester = null;
	
	
	protected Tester getTester() {
		return this.tester;
	}
	
	public int getLexiconSize() {
		return lexiconSize;
	}
	/**
	 * 获得测试准确率
	 */
	public double getPrecision() {
		if (tester == null)
			return -1;
		return tester.calculate().microAverage;
	}
	/**
	 * 清理系统空间
	 */
	public void clear() {
		try {
			((LiblinearTextClassifier)classifier).clear();
		} catch (Exception e) {
		}
		classifier = null;
	}
	
	
	
	
	/**
	 * 测试结果类
	 */
	class TestResult {
		int testSize = 0;
		public double microAverage = 0.0;
		public double macroPrecision = 0.0;
		public double macroRecall = 0.0;
		public double macroFMeasure = 0.0;
		
		public TestResult(){}
		
		public TestResult(TestResult t){
			assign(t);
		}
		
		public void assign(TestResult t) {
			if (t != null) {
				testSize = t.testSize;
				microAverage = t.microAverage;
				macroPrecision = t.macroPrecision;
				macroRecall = t.macroRecall;
				macroFMeasure = t.macroFMeasure;
			}
		}
		
		public String toString() {
			return "Test set size: " + testSize + "\n"
				+ "[MacroAverage]: " 
				+ " Precision: " + macroPrecision
				+ " Recall: " + macroRecall
				+ " FMeasure: " + macroFMeasure 
				+ "\n"
				+ "[MicroAverage]: " + 	microAverage;
		}
	}
	/**
	 * 测试器类
	 */
	protected class Tester {
		
		private int size;
		public double predict[];
		public double answer[];
		public double correct[];
		
		public Tester(int s) {
			size = s;
			predict = new double[s];
			answer = new double[s];
			correct = new double[s];
		}
		
		public double average(double array[]) {
			double sum = 0;
			for (int i = 0; i < array.length; i++)
				sum += array[i];
			return sum / array.length;
		}
		
		public TestResult calculate() {
			double precision[] = new double[predict.length];
			double recall[] = new double[predict.length];
			TestResult result = new TestResult();
			
			for (int i = 0; i < predict.length; i++) {
				result.testSize += predict[i];
				if (correct[i] != 0) {
					precision[i] = correct[i] / predict[i];
					recall[i] = correct[i] / answer[i];
					result.microAverage += correct[i];
				} else {
					precision[i] = 0;
					recall[i] = 0;
				}
			}
			result.macroPrecision = average(precision);
			result.macroRecall = average(recall);
			result.macroFMeasure = 2 * result.macroPrecision * result.macroRecall
							/ (result.macroPrecision + result.macroRecall);
			result.microAverage /= (double)result.testSize;
			return result;
		}
		
		public TestResult calculate(String[] categoryList) {
			if (size != categoryList.length) {
				System.out.println("The size of category list (" + categoryList.length
						+ ") does NOT match size of this Tester (" + size + ") !!!");
				return null;
			}
			double precision[] = new double[predict.length];
			double recall[] = new double[predict.length];
			TestResult result = new TestResult();
			
			for (int i = 0; i < predict.length; i++) {
				result.testSize += predict[i];
				if (correct[i] != 0) {
					precision[i] = correct[i] / predict[i];
					recall[i] = correct[i] / answer[i];
					result.microAverage += correct[i];
				} else {
					precision[i] = 0;
					recall[i] = 0;
				}
				System.out.println(categoryList[i] + ": " + "Precision: " + precision[i]
						+ " Recall: " + recall[i] + " FMeasure: " + 2 * precision[i]
						* recall[i] / (precision[i] + recall[i]));
			}
			result.macroPrecision = average(precision);
			result.macroRecall = average(recall);
			result.macroFMeasure = 2 * result.macroPrecision * result.macroRecall
							/ (result.macroPrecision + result.macroRecall);
			result.microAverage /= (double)result.testSize;
			System.out.println(result.toString());
			return result;
		}
	}

	
	public TextClassifier getTextClassifier() {
		return classifier;
	}
	
	public void setTextClassifier(TextClassifier tc) {
		classifier = tc;
	}
	
	public String getCategoryName (int id) {
		return categoryList.get(id);
	}
	
	public int getCategorySize () {
		return categoryList.size();
	}
	
	
	
	/**
	 * 加载分类列表
	 */
	public boolean loadCategoryList(String categories) {
		categoryList.clear();
		categoryToInt.clear();
		String categoryArr[] = categories.split(" ");
		int i=0;
		for (String str : categoryArr) {
			categoryList.add(str);
			categoryToInt.put(str,i);
			i=i+1;
		}
			
		return true;
	}
	
	/**
	 * 从文件中获取分类列表
	 */
	public boolean loadCategoryListFromFile(String filePath) {
		File f;
		if (filePath == null || !(f = new File(filePath)).exists() || !f.isFile()) {
			System.out.println("load categoryListFromFile failed");
			return false;
		}
		categoryList.clear();
		String s;
		TextFileReader tfr;
		try {
			tfr = new TextFileReader(filePath, encoding);
			while ((s = tfr.readLine()) != null) {
				categoryList.add(s);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		categoryToInt.clear();
		System.out.println("--------------------------------\nCategory List:");
		for (int i = 0; i < categoryList.size(); ++i) {
			categoryToInt.put(categoryList.get(i), i);
			
			System.out.println(i + "\t\t" + categoryList.get(i));
		}
		System.out.println("--------------------------------");
		return true;
	}
	
	/**
	 * 从文件夹中获取分类列表
	 */
	public boolean loadCategoryListFromFolder(String folder) {
		File f;
		if (folder == null || !(f = new File(folder)).exists() || !f.isDirectory())
			return false;
		categoryList.clear();
		File listFiles[] = f.listFiles();
		for (int i = 0 ; i < listFiles.length; ++i)
			if (listFiles[i].isDirectory())
				categoryList.add(listFiles[i].getName());
		
		categoryToInt.clear();
		System.out.println("--------------------------------\nCategory List:");
		for (int i = 0; i < categoryList.size(); ++i) {
			categoryToInt.put(categoryList.get(i), i);
			System.out.println(i + "\t\t" + categoryList.get(i));
		}
		System.out.println("--------------------------------");
		
		return true;
	}

	public static double average(double array[]) {
		double sum = 0;
		for (int i = 0; i < array.length; i++)
			sum += array[i];
		return sum / array.length;
	}
	
	/**
	 * 对训练文件中的文本进行预处理，整理成标准格式
	 */
	public String trainerfilter(String text){
		if(text.length() > 6003){
			text = text.substring(0, 6001);
		}
		char[] chs = new char[text.length()];
		text = LangUtils.mapFullWidthLetterToHalfWidth(text);
		text = LangUtils.mapChineseMarksToAnsi(text);
		text = LangUtils.mapFullWidthNumberToHalfWidth(text);
		text = LangUtils.removeEmptyLines(text);
		text = LangUtils.removeExtraSpaces(text);
		int id = 0;
		for(int i=0;i<text.length();i++){
			char c = text.charAt(i);
			if(LangUtils.isChinese(c)
					 || ((int)c >31&& (int)c<128)
					){
				chs[id]=c;
				id++;
			}
		}
		return LangUtils.T2S(new String(chs).trim());
	}
	
	
	
	/**
	 * 给定类别和文本进行训练
	 */
	
	public boolean addTraining;
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 给定类别，添加训练文本
	 */
	public boolean addTrainingText(String category, String filename) {
		int label = -1;
		if (filename == null) {
			System.err.println("ERROR : AddTrainingText()  filename is NULL !");
			return false;
		}
		if (category == null || !categoryToInt.containsKey(category) || (label = categoryToInt.get(category)) < 0) {
			System.err.println("ERROR : AddTrainingText()  Can't find category: " 
					+ category + " "
					+ categoryToInt.keySet().contains(category) + " " 
					+ categoryToInt.toString() + " " 
					+ categoryToInt.get(category));
			return false;
		}
		String content;
		try {
			content = TextFileReader.readAll(filename, encoding);
			content = trainerfilter(content);
			System.out.println("====>content:"+content);
			System.out.println("====>label:"+label);
			classifier.addTrainingText(content, label);
		} catch (IOException e) {
			System.err.println("ERROR : AddTrainingText()  Can't read content from " + filename);
			return false;
		}
		return true;
	}
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 自动添加训练文件
	 */
	public void addfiles(String filename) {
		if(filename == null)
			return;
		File file = new File(filename);
		if(!file.exists())
			return;
		ArrayList<String> filteredName = new ArrayList<String>();

		if (file.isDirectory()) {
			File[] listFiles = file.listFiles();
			for (int i = 0; i < listFiles.length; ++i) {
				if (listFiles[i].isDirectory()) {
					if (trainingPathIndex >= 0)
						addfiles(listFiles[i].getAbsolutePath());
					else {
						trainingPathIndex = categoryToInt.get(listFiles[i].getName());
						if (trainingPathIndex < 0)
							System.err.println("Can't map " + listFiles[i].getName() + " to any category.");
						else
							addfiles(listFiles[i].getAbsolutePath());
						trainingPathIndex = -1;
					}
				} else if (listFiles[i].isFile() && trainingPathIndex >= 0) {
					if (suffix == null || 
							(suffix.startsWith("NOT") && !listFiles[i].getName().endsWith(suffix.replace("NOT", ""))) ||
							(!suffix.startsWith("NOT") && listFiles[i].getName().endsWith(suffix)))
						filteredName.add(listFiles[i].getAbsolutePath());
				}
			}
		} else if (file.isFile() && trainingPathIndex >= 0) {
			if (suffix == null || 
					(suffix.startsWith("NOT") && !file.getName().endsWith(suffix.replace("NOT", ""))) ||
					(!suffix.startsWith("NOT") && file.getName().endsWith(suffix)))
				filteredName.add(file.getAbsolutePath());
		}
		
		for (int i = 0; i < filteredName.size(); ++i) {
			if((double)i / (double)filteredName.size() > ratio1)
				break;
			addTrainingText(categoryList.get(trainingPathIndex), filteredName.get(i));
			index++;
  			if (printDetail && index % 1000 == 0)
  				printDetail();
		}
	}

	protected void printDetail() {
		System.err.println(index + "  " + System.currentTimeMillis());
	}
	
	/**
	 * 
	 * 对一个文件进行分类，返回前topN个分类结果
	 * 
	 * 如果输入的filepath是文件夹，则只会在Console中打印每个子文件的分类结果，返回值是空数组
	 * 
	 */
	public ClassifyResult[] classifyFile(String filepath, int topN){
		if(filepath == null) {
			System.err.println("ERROR : classifyFile() filepath is null");
			return new ClassifyResult[]{};
		}
		File file = new File(filepath);
		if(!file.exists()) {
			System.err.println("ERROR : classifyFile() " + file.getAbsolutePath() + " not exists!");
			return new ClassifyResult[]{};
		} else if (file.isDirectory()) {
			File[] listFiles = file.listFiles();
			for (int i = 0; i < listFiles.length; ++i)
				classifyFile(listFiles[i].getAbsolutePath(), topN);
			return new ClassifyResult[]{};
		}
		String content = "";
		try {
			content = TextFileReader.readAll(filepath, encoding);
			content = trainerfilter(content);
		} catch (IOException e) {
			System.err.println("ERROR : classifyFile()  Can't read content from " + filepath);
		}
		ClassifyResult[] result = classifier.classify(content, topN);
		System.out.print("Classifying " + filepath + " :  ");
		this.printClassifyResult(result);
		return result;
	}

	/**
	 * 
	 * 对一个文本进行分类，返回前topN个分类结果
	 * 
	 */
	public ClassifyResult[] classifyText(String text, int topN){
		if(topN > categoryList.size()){
			topN = categoryList.size();
		}
		return classifier.classify(text, topN);
	}
	
	/**
	 * 对文件进行自动分类测试
	 */	
	public void testfiles(String filename) {
		if(filename == null)
			return;
		File file = new File(filename);
		if(!file.exists())
			return;
		ArrayList<String> filteredName = new ArrayList<String>();

		if (file.isDirectory()) {
			File[] listFiles = file.listFiles();
			for (int i = 0; i < listFiles.length; ++i) {
				if (listFiles[i].isDirectory()) {
					if (testingPathIndex >= 0)
						testfiles(listFiles[i].getAbsolutePath());
					else {
						testingPathIndex = categoryToInt.get(listFiles[i].getName());
						if (testingPathIndex < 0)
							System.err.println("Can't map " + listFiles[i].getName() + " to any category.");
						else
							testfiles(listFiles[i].getAbsolutePath());
						testingPathIndex = -1;
					}
				}  else if (listFiles[i].isFile() && testingPathIndex >= 0) {
					if (suffix == null || 
						(suffix.startsWith("NOT") && !listFiles[i].getName().endsWith(suffix.replace("NOT", ""))) ||
						(!suffix.startsWith("NOT") && listFiles[i].getName().endsWith(suffix)))
						filteredName.add(listFiles[i].getAbsolutePath());
				}
			}
		} else if (file.isFile() && testingPathIndex >= 0) {
			if (suffix == null || 
					(suffix.startsWith("NOT") && !file.getName().endsWith(suffix.replace("NOT", ""))) ||
					(!suffix.startsWith("NOT") && file.getName().endsWith(suffix)))
				filteredName.add(file.getAbsolutePath());
		}

		for (int i = 0; i < filteredName.size(); ++i) {
			if((double)i / (double)filteredName.size() < 1 - ratio2)
				continue;
			
			if (filteredName.get(i).endsWith("eng")) continue;
				
			String content = null;
			try {
				content = TextFileReader.readAll(filteredName.get(i), encoding);
			} catch (IOException e) {
				System.err.println("ERROR : testfiles()  Can't read content from " + filteredName.get(i));
			}
			content = trainerfilter(content);
			ClassifyResult result = classifier.classify(content);
			if(result != null){
				tester.predict[result.label]++;
				tester.answer[testingPathIndex]++;
				if (testingPathIndex == result.label) {
					if(printDetail)
						System.out.println("Right!" + filteredName.get(i) + " "
								+ categoryList.get(result.label) + " "
								+ categoryList.get(testingPathIndex));
					tester.correct[result.label]++;
				} else if (printDetail){
					System.out.println("Wrong!" + filteredName.get(i) + " "
							+ categoryList.get(result.label) + " "
							+ categoryList.get(testingPathIndex));
				}
			}
			index ++;
		}
	}
	/**
	 * 打印分类结果
	 */
	protected void printClassifyResult (ClassifyResult[] result) {
		if (categoryList.size() == 0) 
			for (int i = 0; i < result.length; ++i)
				System.out.println(result[i].toString());
		else
			for (int i = 0; i < result.length; ++i)
				System.out.println(categoryList.get(result[i].label) + "\t" + result[i].prob);
	}
	
	public static void exit(String message) {
		System.err.println("ERROR : \t" + message + "\nForce Quit .");
		System.exit(1);
	}

	public void Init(int categorySize) {
		String usage = "org.thunlp.text.classifiers.BasicTextClassifier \n"
			+ " [-c CATEGORY_LIST_FILE_PATH]\t从文件中读入类别信息。该文件中每行包含且仅包含一个类别名称。\n"
			+ " [-train TRAIN_PATH]\t进行训练，并设置训练语料文件夹路径。该文件夹下每个子文件夹的名称都对应一个类别名称，内含属于该类别的训练语料。若不设置，则不进行训练。\n"
			+ " [-eval EVAL_PATH]\t进行评测，并设置评测语料文件夹路径。该文件夹下每个子文件夹的名称都对应一个类别名称，内含属于该类别的评测语料。若不设置，则不进行评测。也可以使用-test。\n"
			+ " [-classify FILE_PATH]\t对一个文件进行分类。\n"
			+ " [-n topN]\t设置返回候选分类数，按得分大小排序。默认为1，即只返回最可能的分类。\n"
			+ " [-svm libsvm or liblinear]\t选择使用libsvm还是liblinear进行训练和测试，默认使用liblinear\n"
			+ " [-l LOAD_MODEL_PATH]\t设置读取模型路径。\n"
			+ " [-s SAVE_MODEL_PATH]\t设置保存模型路径。\n"
			+ " [-f FEATURE_SIZE]\t设置保留特征数目，默认为5000。\n"
			+ " [-d1 RATIO]\t设置训练集占总文件数比例，默认为0.8。\n"
			+ " [-d2 RATIO]\t设置测试集占总文件数比例，默认为0.2。\n"
			+ " [-e ENCODING]\t设置训练及测试文件编码，默认为UTF-8。\n"
			+ " [-filter SUFFIX]\t设置文件后缀过滤。例如设置“-filter .txt”，则训练和测试时仅考虑文件名后缀为.txt的文件。\n"
			;
		//训练文件全部用于训练 不作测试
		ratio1=1;
		ratio2=0;
		/****************************************************************************************/
		
		if (linear)
			setTextClassifier(new LinearBigramChineseTextClassifier(categorySize));
		else 
			setTextClassifier(new BigramChineseTextClassifier(categorySize));
	}

	/**
	 * 根据输入参数，运行分类系统
	 */
	public String runAsBigramChineseTextClassifier(List<AnalysisProjectClassifyTraining> trainings) {
		TextClassifier classifier = null;
		if(linear)
			classifier = (LinearBigramChineseTextClassifier) this.getTextClassifier();
		else 
			classifier = (BigramChineseTextClassifier) this.getTextClassifier();
		
		
		
		
		//加载训练集数据
		for (AnalysisProjectClassifyTraining training : trainings) {
			classifier.addTrainingText(training.getText(), categoryToInt.get(training.getCategoryName()));
		}
		
			lexiconSize = classifier.getLexicon().getSize();
			classifier.train();
			modelLoaded = true;
			//将模型输出成string
			return classifier.saveToString();
			
			

		
		
		
	}
	
	
	
	
}
