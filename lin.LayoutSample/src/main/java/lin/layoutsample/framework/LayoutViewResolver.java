package lin.layoutsample.framework;

import lin.layoutsample.utils.FileUtils;
import lin.layoutsample.utils.StringUtils;

import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

public class LayoutViewResolver extends InternalResourceViewResolver {

	private static String DEFUALT_YIELD_CMD = "${yield}";
	private static String DEFUALT_LAYOUT_TEMP_DIR = "/WEB-INF/layout/layout_temp";
	private static String DEFUALT_LAYOUT = "/WEB-INF/layout/layout.jsp";

	/**
	 * layout文件。默认为/WEB-INF/layout/layout.jsp。
	 * */
	private String layout = DEFUALT_LAYOUT;

	/**
	 * 经过替换后的视图缓存目录，默认为/WEB-INF/layout/layout_temp。
	 * */
	private String temp_path = DEFUALT_LAYOUT_TEMP_DIR;

	/**
	 * 默认为${yield}。模板中该命令会被子视图替换。类似rails的yield。
	 * */
	private String yield_cmd = DEFUALT_YIELD_CMD;

	/**
	 * 
	 * 函数首先根据指定的view在缓存区生成对应的文件，然后将模板文件（默认为layout.jsp）中的${yield}替换成视图的内容。
	 * 再调用超类的buildview函数生成新的被视图并返回。这样就完成了模板的替换和重定向到新生成的视图。
	 * */
	@Override
	protected AbstractUrlBasedView buildView(String viewName) throws Exception {
		String url = getPrefix() + viewName + getSuffix();
		String target_view_real_path = super.getServletContext().getRealPath(
				getTemp_path())
				+ url;
		FileUtils.createFile(target_view_real_path);
		String layout_real_path = super.getServletContext().getRealPath(
				getLayout());
		String layout_content = FileUtils.read(layout_real_path);
		String view_content = FileUtils.read(super.getServletContext()
				.getRealPath(url));
		String result_view_content = StringUtils.replace(layout_content, "\\Q"
				+ getYield_cmd() + "\\E", view_content);
		FileUtils.write(target_view_real_path, result_view_content);

		AbstractUrlBasedView view = super.buildView(getTemp_path() + getPrefix()+ viewName);
		return view;
	}

	public String getLayout() {
		return layout;
	}

	public void setLayout(String layout) {
		this.layout = layout;
	}

	public String getTemp_path() {
		return temp_path;
	}

	public void setTemp_path(String temp_path) {
		this.temp_path = temp_path;
	}

	public String getYield_cmd() {
		return yield_cmd;
	}

	public void setYield_cmd(String yield_cmd) {
		this.yield_cmd = yield_cmd;
	}
}
