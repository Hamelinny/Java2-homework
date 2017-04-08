## MyGit
Приложение реализует систему контроля версий.

### Сборка jar-файла
Чтобы собрать отдельный jar-файл, в папке MyGit вызовите `gradlew createJar`. Собранный jar-файл будет в папке MyGit/build/libs.

### Работа с приложением
Осуществляется через консоль.

Обрабатываются следующие команды:

* **init** -- создаёт репозиторий
* **add** -- добавляет файлы в отслеживаемые в index-е
* **commit** -- создаёт новый коммит, в которые входят все файлы, отслеживаемые в index-е
* **branch** -- создаёт новую ветку; если указать параметр -d, то удаляет
* **checkout** -- переключается на соответствющую ветку; если указать в качестве параметра хеш коммита, то создаёт новую ветку с таким названием и переключается на неё
* **merge** -- сливает указанную ветку с текущей
* **log** -- выводит все логи, связанные с текущей веткой
