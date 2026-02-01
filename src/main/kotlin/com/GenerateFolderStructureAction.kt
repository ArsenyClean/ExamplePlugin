package com
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VirtualFile
import java.util.*




class GenerateFolderStructureAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
//      тут мы используем созданный нами ранее диалог
        val dialog = InputDialog()
//      показываем его
        dialog.show()
        ApplicationManager.getApplication().runWriteAction {
            if (dialog.isOK) {
//              достаем введенное пользователем название
                val featureName = dialog.featureName
//              получаем системный путь где начать создание шаблона
                val libDir = event.getData(PlatformDataKeys.VIRTUAL_FILE)
                generateFolderStructure(libDir, featureName)
            }
        }


    }


    private fun generateFolderStructure(libDir: VirtualFile?, featureName: String) {
        if (libDir != null) {
//          создаем подпапку с введеным пользователем названием
            val featureDir = libDir.createChildDirectory(null, featureName)


//          data


            val dataDir = featureDir.createChildDirectory(null, "data")
//          в созданной директории создаем файл "${featureName}_data_source.dart" - расширение файла и его содержимое могут быть любыми
//          .createChildData создает файл с указаным именем и расширениием
//          .setBinaryContent записывает нужный нам текст внутрь созданного файла
            dataDir.createChildDirectory(null, "data_source").createChildData(null, "${featureName}_data_source.dart")
                .setBinaryContent(getDataSourceContent(featureName).toByteArray())

//          ... тут можно дальше создавать новые директории и файлы

//          показываем диалог с успешным завершением генерации шаблона
            showToastMessage("Generated Successfully!")


        }
    }


    private fun String.toCamelCase(): String {
        return this.split("_")
            .joinToString("") { it.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } }
    }


    private fun showToastMessage(message: String) {
        ApplicationManager.getApplication().invokeLater {
            Messages.showMessageDialog(message, "Success", Messages.getInformationIcon())
        }
    }


    private fun getDataSourceContent(featureName: String): String {
//      при записи в файл | - указывает начало строки, чтобы у содержимого сохранилось форматирование
        return """
           |final class ${featureName.toCamelCase()}DataSource {
           |  const ${featureName.toCamelCase()}DataSource({required NetworkService service}) : _service = service;
           |
           | final NetworkService _service;
           |
           | }
           |
       """.trimMargin()
    }


}