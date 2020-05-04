package com.prototype.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 *
 * @author mweigel
 */
public class IterateFiles {

    private static void fetchFiles(File dir, Consumer<File> fileConsumer) {
        if (dir.isDirectory()) {
            for (File file1 : dir.listFiles()) {
                fetchFiles(file1, fileConsumer);
            }
        } else {
            fileConsumer.accept(dir);
        }
    }

    public static Collection<String> getTargetFiles(String root, String... targets) {
        Map<String, String> targetMap = new HashMap<>();
        File file = new File(root);

        Consumer<File> collect = fileFound -> {
            String fileName = fileFound.getAbsolutePath();
            if(targets.length == 2){
                if (fileName.contains(targets[0]) && fileName.contains(targets[1])) {
                    // Get unique list from map later
                   String dir = fileName.substring(0, fileName.indexOf(targets[0])+targets[0].length());
                   //System.out.println("dir = " + dir);
                   targetMap.put(dir, dir);
                }
            }
        };

        fetchFiles(file, collect);

        return targetMap.values();
    }
}
