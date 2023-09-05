package com.xizhe;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author admin
 * @version 1.0
 * @description: TODO
 * @date 2023/9/4 20:10
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ZookeeperNode {

    private String nodePath;

    private byte[] data;
}
